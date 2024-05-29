package tech.insight.engine.innodb.index.scan

import java.nio.ByteBuffer
import tech.insight.core.bean.Row
import tech.insight.core.plan.ExplainType
import tech.insight.engine.innodb.index.InnodbIndex
import tech.insight.engine.innodb.page.ConstantSize
import tech.insight.engine.innodb.page.InnoDbPage
import tech.insight.engine.innodb.page.InnoDbPage.Companion.findPageByOffset
import tech.insight.engine.innodb.page.InnodbUserRecord
import tech.insight.engine.innodb.page.Supremum
import tech.insight.engine.innodb.page.compact.Compact
import tech.insight.engine.innodb.page.type.DataPage


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class AllScannerCursor(override val index: InnodbIndex) : ScannerCursor {

    override val explainType: ExplainType = ExplainType.ALL

    private var currentPage: InnoDbPage

    private var currentOffset: Short = 0

    private var currentRow: InnodbUserRecord? = null

    init {
        var page = index.rootPage
        while (page.pageType() !is DataPage) {
            val firstUserRecord = page.getFirstUserRecord()
            val offset = ByteBuffer.wrap((firstUserRecord as Compact).point()).getInt()
            page = findPageByOffset(offset, page.ext.belongIndex)
        }
        this.currentPage = page
        this.currentOffset = ConstantSize.INFIMUM.offset().toShort()
    }


    override fun close() {
        // don't need close the cursor
    }

    override fun hasNext(): Boolean {
        if (currentRow != null) {
            return true
        }
        findNext()
        return currentRow != null
    }

    override fun next(): Row {
        if (currentRow != null) {
            val result = currentRow
            currentRow = null
            return result!!
        }
        findNext()
        return currentRow?: throw NoSuchElementException("no next row")
    }

    private fun findNext() {
        while (currentPage.pageHeader.recordCount.toInt() == 0) {
            if (currentPage.fileHeader.next == 0) {
                //  empty root page node
                return
            }
            currentPage = findPageByOffset(currentPage.fileHeader.next, currentPage.ext.belongIndex)
        }
        if (currentOffset == ConstantSize.INFIMUM.offset().toShort()) {
            val row = currentPage.getFirstUserRecord()
            currentRow = row
            currentOffset = row.absoluteOffset().toShort()
            return
        }
        if (currentOffset == ConstantSize.SUPREMUM.offset().toShort()) {
            currentPage = findPageByOffset(currentPage.fileHeader.next, currentPage.ext.belongIndex)
            currentOffset = ConstantSize.INFIMUM.offset().toShort()
            return findNext()
        }
        val nextRow = currentPage.getUserRecordByOffset(currentOffset.toInt())
        if (nextRow is Supremum) {

        }

    }
}
