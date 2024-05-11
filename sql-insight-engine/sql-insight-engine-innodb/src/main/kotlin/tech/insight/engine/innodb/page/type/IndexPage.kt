package tech.insight.engine.innodb.page.type

import tech.insight.core.bean.Column
import tech.insight.core.bean.value.Value
import tech.insight.core.bean.value.ValueNull
import tech.insight.engine.innodb.page.IndexNode
import tech.insight.engine.innodb.page.InnoDbPage
import tech.insight.engine.innodb.page.InnoDbPage.Companion.findPageByOffset
import tech.insight.engine.innodb.page.InnodbUserRecord
import tech.insight.engine.innodb.page.SystemUserRecord
import tech.insight.engine.innodb.page.compact.IndexRecord
import tech.insight.engine.innodb.page.compact.RecordHeader
import tech.insight.engine.innodb.page.compact.RowFormatFactory
import tech.insight.engine.innodb.utils.ValueNegotiator
import java.nio.ByteBuffer


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class IndexPage(override val page: InnoDbPage) : PageType {


    override val value: Short = FIL_PAGE_INODE

    override fun locatePage(userRecord: InnodbUserRecord): InnoDbPage {
        val targetSlot = page.findTargetSlot(userRecord)
        var firstIndex = page.targetSlotFirstUserRecord(targetSlot)
        //   todo Whether it is better to allow nodes to implement comparison functions?
        while (compare(userRecord, firstIndex as IndexRecord) > 0) {
            firstIndex = page.getUserRecordByOffset(firstIndex.offset() + firstIndex.nextRecordOffset())
        }
        val targetIndex = firstIndex
        return findPageByOffset(targetIndex.indexNode().pointer, page.ext.belongIndex)
    }

    override fun pageIndex(): IndexRecord {
        val firstData = this.page.getUserRecordByOffset(page.infimum.offset() + page.infimum.nextRecordOffset())
        return firstData as IndexRecord
    }


    override fun convertUserRecord(offsetInPage: Int): IndexRecord {
        //  todo dynamic primary key
        val columns: List<Column> = page.ext.belongIndex.columns()
        val recordHeader: RecordHeader = RowFormatFactory.readRecordHeader(page, offsetInPage)
        val key: Array<Value<*>> = Array(columns.size) { ValueNull }
        val buffer = ByteBuffer.wrap(page.toBytes(), offsetInPage, page.length() - offsetInPage)
        for (i in key.indices) {
            val column: Column = columns[i]
            val valueArr = ByteArray(column.length)
            buffer[valueArr]
            key[i] = ValueNegotiator.wrapValue(column, valueArr)
        }
        return IndexRecord(recordHeader, IndexNode(key, buffer.getInt()), page.ext.belongIndex)
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

    companion object {
        const val FIL_PAGE_INODE = 0x0003.toShort()
    }

}
