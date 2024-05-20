package tech.insight.engine.innodb.page.type

import tech.insight.core.bean.Column
import tech.insight.core.bean.ReadRow
import tech.insight.core.bean.Table
import tech.insight.core.bean.value.Value
import tech.insight.core.bean.value.ValueInt
import tech.insight.engine.innodb.page.*
import tech.insight.engine.innodb.page.compact.Compact
import tech.insight.engine.innodb.page.compact.CompactNullList
import tech.insight.engine.innodb.page.compact.RowFormatFactory.readRecordHeader
import tech.insight.engine.innodb.page.compact.Variables
import tech.insight.engine.innodb.page.type.IndexPage.Companion.FIL_PAGE_INODE
import tech.insight.engine.innodb.utils.PageSupport
import tech.insight.engine.innodb.utils.RowComparator
import tech.insight.engine.innodb.utils.ValueNegotiator
import java.nio.ByteBuffer
import java.util.*


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class DataPage(override val page: InnoDbPage) : PageType {

    override val value: Short = FIL_PAGE_INDEX_VALUE


    /**
     * This page should be returned whether it is a root page or a downward call from the parent index page
     */
    override fun locatePage(userRecord: InnodbUserRecord): InnoDbPage {
        return page
    }

    /**
     * read page source solve a user record.
     * the offset is offset in page.
     * offset is after record header .in other words offset - record header size  means record header offset
     */
    override fun convertUserRecord(offsetInPage: Int): InnodbUserRecord {
        if (ConstantSize.INFIMUM.offset() == offsetInPage) {
            return page.infimum
        }
        if (ConstantSize.SUPREMUM.offset() == offsetInPage) {
            return page.supremum
        }
        val compact = Compact()
        compact.offsetInPage = (offsetInPage)
        compact.recordHeader = (readRecordHeader(page, offsetInPage))
        val table = page.ext.belongIndex.belongTo()
        fillNullAndVar(page, offsetInPage, compact, table)
        val variableLength: Int = compact.variables.variableLength()
        val fixLength = compactFixLength(compact, table)
        val body: ByteArray =
            Arrays.copyOfRange(page.toBytes(), offsetInPage, offsetInPage + variableLength + fixLength)
        compact.body = (body)
        compact.sourceRow = (compactReadRow(compact, table))
        compact.belongIndex = (page.ext.belongIndex)
        compact.belongPage = this.page
        return compact
    }

    override fun pageIndex(): InnodbUserRecord {
        val firstData = page.getUserRecordByOffset(page.infimum.absoluteOffset() + page.infimum.nextRecordOffset())
        return firstData.indexNode()
    }


    /**
     * if root page is data page, b plus tree height must be 1.
     * so root page upgrade to index page and link to two data page.
     */
    override fun rootUpgrade(leftPage: InnoDbPage, rightPage: InnoDbPage) {
        val firstOffset: Int = PageSupport.allocatePage(page.ext.belongIndex, 2)
        val secondOffset = firstOffset + ConstantSize.PAGE.size()
        leftPage.apply {
            pageHeader.level = 0
            pageHeader.indexId = page.pageHeader.indexId
            fileHeader.offset = firstOffset
            fileHeader.next = secondOffset
        }
        rightPage.apply {
            pageHeader.level = 0
            pageHeader.indexId = page.pageHeader.indexId
            fileHeader.offset = secondOffset
            fileHeader.pre = firstOffset
        }
        //  transfer to index page and clear root page
        page.apply {
            pageHeader = PageHeader.create()
            pageHeader.level++
            pageDirectory = PageDirectory()
            fileHeader.pageType = FIL_PAGE_INODE
            userRecords = UserRecords()
            infimum = Infimum.create(this)
            supremum = Supremum.create(this)
            PageSupport.flushPage(this)
            insertData(leftPage.pageIndex())
            insertData(rightPage.pageIndex())
        }
    }

    override fun compare(o1: InnodbUserRecord, o2: InnodbUserRecord): Int {
        return RowComparator.primaryKeyComparator().compare(o1, o2)
    }


    /**
     * fill compact field null list and variables
     * depend on table info.
     */
    private fun fillNullAndVar(page: InnoDbPage, offset: Int, compact: Compact, table: Table) {
        val nullBytesEnd = offset - ConstantSize.RECORD_HEADER.size()
        val nullLength: Int = CompactNullList.calcNullListLength(table.ext.nullableColCount)
        val pageArr: ByteArray = page.toBytes()
        val nullStart = nullBytesEnd - nullLength
        val nullListByte = Arrays.copyOfRange(pageArr, nullStart, nullBytesEnd)
        //   read null list
        val compactNullList = CompactNullList.wrap(nullListByte)
        compact.nullList = (compactNullList)
        //   read variable
        val variableCount = variableColumnCount(table, compactNullList)
        val varStart = nullStart - variableCount
        val variableArray = Arrays.copyOfRange(pageArr, varStart, nullStart)
        compact.variables = (Variables(variableArray))
    }


    private fun variableColumnCount(table: Table, nullList: CompactNullList): Int {
        val columnList: List<Column> = table.columnList
        var result = 0
        for (column in columnList) {
            if (!column.notNull && nullList.isNull(column.nullListIndex)) {
                continue
            }
            if (column.variable) {
                result++
            }
        }
        return result
    }

    private fun compactFixLength(compact: Compact, table: Table): Int {
        var fixLength = 0
        for (column in table.columnList) {
            if (column.variable) {
                continue
            }
            if (column.notNull || !compact.nullList.isNull(column.nullListIndex)) {
                fixLength += column.length
            }
        }
        return fixLength
    }

    private fun compactReadRow(compact: Compact, table: Table): ReadRow {
        val columnList: List<Column> = table.columnList
        val valueList: MutableList<Value<*>> = ArrayList<Value<*>>(columnList.size)
        val bodyBuffer = ByteBuffer.wrap(compact.body)
        val iterator: Iterator<Byte> = compact.variables.iterator()
        var rowId = -1
        for (column in columnList) {
            if (!column.notNull && compact.nullList.isNull(column.nullListIndex)) {
                valueList.add(column.defaultValue)
                continue
            }
            val length = if (column.variable) iterator.next().toInt() else column.length
            val item = ByteArray(length)
            bodyBuffer[item]
            val value: Value<*> = ValueNegotiator.wrapValue(column, item)
            valueList.add(value)
            if (column.primaryKey) {
                rowId = (value as ValueInt).source
            }
        }
        val row = ReadRow(valueList, rowId.toLong())
        row.table = table
        return row
    }

    companion object {
        const val FIL_PAGE_INDEX_VALUE = 0X45bf.toShort()

    }

}
