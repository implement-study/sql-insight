package tech.insight.engine.innodb.page.type

import java.nio.ByteBuffer
import java.util.*
import tech.insight.core.bean.ReadRow
import tech.insight.core.bean.value.Value
import tech.insight.core.bean.value.ValueNull
import tech.insight.engine.innodb.index.InnodbIndex
import tech.insight.engine.innodb.page.ConstantSize
import tech.insight.engine.innodb.page.InnoDbPage
import tech.insight.engine.innodb.page.InnoDbPage.Companion.findPageByOffset
import tech.insight.engine.innodb.page.InnodbUserRecord
import tech.insight.engine.innodb.page.SystemUserRecord
import tech.insight.engine.innodb.page.compact.Compact
import tech.insight.engine.innodb.page.compact.CompactNullList
import tech.insight.engine.innodb.page.compact.RecordType
import tech.insight.engine.innodb.page.compact.Variables
import tech.insight.engine.innodb.utils.ValueNegotiator


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class IndexPage(override val page: InnoDbPage) : PageType {

    override val value = FIL_PAGE_INODE

    override fun locatePage(userRecord: InnodbUserRecord): InnoDbPage {
        return when (userRecord.recordHeader.recordType) {
            RecordType.NORMAL -> doLocateDown(userRecord)
            RecordType.PAGE -> doLocateIndex(userRecord)
            RecordType.INFIMUM -> throw IllegalArgumentException("infimum can't be locate")
            RecordType.SUPREMUM -> throw IllegalArgumentException("supremum can't be locate")
            else -> throw IllegalArgumentException("unknown record type")
        }
    }

    override fun pageIndex(): InnodbUserRecord {
        return this.page.getFirstUserRecord()
    }


    /**
     * if root page need up grade, page header level is tree height
     */
    override fun rootUpgrade(leftPage: InnoDbPage, rightPage: InnoDbPage) {
        leftPage.apply {
            pageHeader.level = page.pageHeader.level
            pageHeader.indexId = page.pageHeader.indexId
            fileHeader.next = rightPage.fileHeader.offset
            fileHeader.pageType = FIL_PAGE_INODE
        }
        rightPage.apply {
            pageHeader.level = page.pageHeader.level
            pageHeader.indexId = page.pageHeader.indexId
            fileHeader.pre = leftPage.fileHeader.offset
            fileHeader.pageType = FIL_PAGE_INODE
        }
        //  clear root page
        page.apply {
            page.clear()
            pageHeader.level++
            insertData(leftPage.pageIndex())
            insertData(rightPage.pageIndex())
        }
    }

    override fun upgrade(otherPage: InnoDbPage) {
        TODO("Not yet implemented")
    }


    override fun convertUserRecord(offsetInPage: Int): InnodbUserRecord {
        if (page.infimum.offsetInPage() == offsetInPage) {
            return page.infimum
        }
        if (page.supremum.offsetInPage() == offsetInPage) {
            return page.supremum
        }
        val belongIndex = page.ext.belongIndex
        val compact = Compact()
        compact.offset = (offsetInPage)
        compact.recordHeader = page.readRecordHeader(offsetInPage)
        fillNullAndVar(page, offsetInPage, compact, belongIndex)
        val variableLength: Int = compact.variables.variableLength()
        val fixLength = run {
            var nullIndex = -1
            var length = 0
            belongIndex.columns().forEach {
                var isNull = false
                if (!it.notNull) {
                    nullIndex++
                    isNull = compact.nullList.isNull(nullIndex)
                }
                if (!it.variable && !isNull) {
                    length += it.length
                }
            }
            length
        }
        val bodyLength = variableLength + fixLength + Int.SIZE_BYTES
        compact.body = Arrays.copyOfRange(page.toBytes(), offsetInPage, offsetInPage + bodyLength)
        compact.sourceRow = (compactIndexReadRow(compact, belongIndex))
        compact.belongIndex = belongIndex
        compact.parentPage = this.page
        compact.setOffsetInPage(offsetInPage)
        return compact
    }

    private fun fillNullAndVar(page: InnoDbPage, offsetInPage: Int, compact: Compact, index: InnodbIndex) {
        val nullBytesEnd = offsetInPage - ConstantSize.RECORD_HEADER.size
        val pageArr: ByteArray = page.toBytes()
        compact.nullList = run {
            val nullListLength = CompactNullList.allocate(index).length()
            val nullListBytes = Arrays.copyOfRange(pageArr, nullBytesEnd - nullListLength, nullBytesEnd)
            CompactNullList.wrap(nullListBytes)
        }
        //   read variable
        val variableCount = run {
            var count = 0
            var nullIndex = -1
            index.columns().forEach {
                if (!it.notNull) {
                    nullIndex++
                }
                if (it.variable && !compact.nullList.isNull(nullIndex)) {
                    count++
                }
            }
            count
        }
        val varStart = nullBytesEnd - compact.nullList.length() - variableCount
        val variableArray = Arrays.copyOfRange(pageArr, varStart, nullBytesEnd - compact.nullList.length())
        compact.variables = Variables.wrap(variableArray)
    }


    override fun compare(o1: InnodbUserRecord, o2: InnodbUserRecord): Int {
        if (o1 is SystemUserRecord) {
            return o1.compareTo(o2)
        }
        if (o2 is SystemUserRecord) {
            return -o2.compareTo(o1)
        }
        val values1 = o1.indexKey()
        val values2 = o2.indexKey()
        for (i in values1.indices) {
            val compare: Int = values1[i].compareTo(values2[i])
            if (compare != 0) {
                return compare
            }
        }
        return 0
    }

    private fun doLocateIndex(userRecord: InnodbUserRecord): InnoDbPage {
        //  if page is empty , return self
        if (page.pageHeader.recordCount == 0) {
            return page
        }
        if (page.pageHeader.level == 1) {
            return page
        }
        return doLocateDown(userRecord)
    }

    private fun doLocateDown(userRecord: InnodbUserRecord): InnoDbPage {
        val targetSlot = page.pageDirectory.findTargetIn(userRecord)
        val firstIndex = targetSlot.minRecord()
        var preRecord = firstIndex
        //   todo Whether it is better to allow nodes to implement comparison functions?
        while (compare(userRecord, preRecord) > 0) {
            val nextOffset = preRecord.offsetInPage() + preRecord.nextRecordOffset()
            val nextRecord = page.getUserRecordByOffset(nextOffset)
            if (nextRecord is SystemUserRecord) {
                break
            }
            preRecord = nextRecord
        }
        val targetIndex = preRecord
        val offset = ByteBuffer.wrap((targetIndex as Compact).point()).getInt()
        val downPage = findPageByOffset(offset, page.ext.belongIndex)
        downPage.ext.parent = page
        return downPage.locatePage(userRecord)
    }

    private fun compactIndexReadRow(compact: Compact, index: InnodbIndex): ReadRow {
        val valueList: MutableList<Value<*>> = ArrayList<Value<*>>(index.columns().size)
        var nullIndex = -1
        val bodyBuffer = ByteBuffer.wrap(compact.body)
        val iterator = compact.variables.iterator()
        index.columns().forEach {
            var isNull = false
            if (!it.notNull) {
                nullIndex++
                isNull = compact.nullList.isNull(nullIndex)
            }
            val addedValue = run {
                if (isNull) {
                    ValueNull
                }
                val length = if (it.variable) iterator.next().toInt() else it.length
                val item = ByteArray(length)
                bodyBuffer[item]
                ValueNegotiator.wrapValue(it, item)
            }
            valueList.add(addedValue)
        }
        val row = ReadRow(valueList, -1)
        row.table = index.table()
        return row
    }

    companion object {
        const val FIL_PAGE_INODE = 0x0003
    }

}
