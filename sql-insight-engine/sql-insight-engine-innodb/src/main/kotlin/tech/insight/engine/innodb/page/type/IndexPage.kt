package tech.insight.engine.innodb.page.type

import tech.insight.core.bean.Column
import tech.insight.core.bean.value.Value
import tech.insight.core.bean.value.ValueNull
import tech.insight.engine.innodb.page.ConstantSize
import tech.insight.engine.innodb.page.IndexNode
import tech.insight.engine.innodb.page.InnoDbPage
import tech.insight.engine.innodb.page.InnoDbPage.Companion.createIndexPage
import tech.insight.engine.innodb.page.InnodbUserRecord
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

    override fun doInsertData(data: InnodbUserRecord) {
        TODO("Not yet implemented")
    }

    override fun pageSplitIfNecessary() {
        if (this.freeSpace.toInt() > ConstantSize.PAGE.size() shr 5) {
            return
        }
        val allRecords: MutableList<InnodbUserRecord> = ArrayList(pageHeader.recordCount + 1)
        var base: InnodbUserRecord = infimum
        while (true) {
            base = getUserRecordByOffset(base.offset() + base.nextRecordOffset())
            if (base === supremum) {
                break
            }
            allRecords.add(base)
        }
        val pre: InnoDbPage = createIndexPage(allRecords.subList(0, allRecords.size / 2), ext.belongIndex)
        val next: InnoDbPage = createIndexPage(
            allRecords.subList(allRecords.size / 2, allRecords.size),
            ext.belongIndex
        )
        upgrade(pre, next)
    }

    override fun locatePage(userRecord: InnodbUserRecord): InnoDbPage {
        TODO("Not yet implemented")
    }

    override fun pageIndex(): IndexRecord {
        val firstData = this.page.getUserRecordByOffset(page.infimum.offset() + page.infimum.nextRecordOffset())
        val columns: List<Column> = page.ext.belongIndex.columns()
        val values = columns.map { it.name }.map { firstData.getValueByColumnName(it) }.toTypedArray()
        return IndexRecord(IndexNode(values, page.fileHeader.offset), page.ext.belongIndex)
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
        require(o1 is IndexRecord && o2 is IndexRecord) { "index page only support compare index record" }
        val values1 = o1.indexNode().key
        val values2 = o2.indexNode().key
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
