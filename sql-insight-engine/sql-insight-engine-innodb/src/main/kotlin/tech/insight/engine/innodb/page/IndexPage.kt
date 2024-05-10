package tech.insight.engine.innodb.page

import tech.insight.core.bean.Column
import tech.insight.core.bean.value.Value
import tech.insight.core.bean.value.ValueNull
import tech.insight.engine.innodb.index.InnodbIndex
import tech.insight.engine.innodb.page.compact.IndexRecord
import tech.insight.engine.innodb.page.compact.RecordHeader
import tech.insight.engine.innodb.page.compact.RowFormatFactory
import tech.insight.engine.innodb.utils.ValueNegotiator
import java.nio.ByteBuffer

/**
 *
 * index page
 *
 * @author gongxuanzhangmelt@gmail.com
 */
class IndexPage(index: InnodbIndex) : InnoDbPage(index) {

    override fun insertData(data: InnodbUserRecord) {
        if (data is IndexRecord) {
            super.insertData(data)
            return
        }
        val preAndNext = findPreAndNext(data)
        val pre = preAndNext.first
        val next = preAndNext.second
        val hit: IndexRecord = if (pre is Infimum) {
            next as IndexRecord
        } else {
            pre as IndexRecord
        }
        val pointPage: InnoDbPage = findPageByOffset(hit.indexNode().pointer, ext.belongIndex)
        pointPage.insertData(data)
    }



    /**
     * data page will split when free space less than one in thirty-two page size
     */
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

}
