package tech.insight.engine.innodb.index.scan

import java.nio.ByteBuffer
import tech.insight.core.bean.Row
import tech.insight.core.plan.ExplainType
import tech.insight.engine.innodb.index.InnodbIndex
import tech.insight.engine.innodb.page.ConstantSize
import tech.insight.engine.innodb.page.InnoDbPage
import tech.insight.engine.innodb.page.InnoDbPage.Companion.findPageByOffset
import tech.insight.engine.innodb.page.InnodbUserRecord
import tech.insight.engine.innodb.page.compact.Compact
import tech.insight.engine.innodb.page.type.DataPage


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class AllScannerCursor(override val index: InnodbIndex) : ScannerCursor {

    override val explainType: ExplainType = ExplainType.ALL

    private var currentPage: InnoDbPage

    /**
     * next row's offset, currentRow may be null.
     */
    private var nextOffset: Short = 0

    private var currentRow: InnodbUserRecord? = null

    init {
        var page = index.rootPage
        while (page.pageType() !is DataPage) {
            val firstUserRecord = page.getFirstUserRecord()
            val offset = ByteBuffer.wrap((firstUserRecord as Compact).point()).getInt()
            page = findPageByOffset(offset, page.ext.belongIndex)
        }
        this.currentPage = page
        this.nextOffset = ConstantSize.INFIMUM.offset().toShort()
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
            val result = currentRow!!
            currentRow = null
            nextOffset = (result.absoluteOffset() + result.nextRecordOffset()).toShort()
            return (result as Compact)/*.sourceRow*/
        }
        findNext()
        if (currentRow == null) {
            throw NoSuchElementException("no next row")
        }
        val result = currentRow!!
        currentRow = null
        nextOffset = (result.absoluteOffset() + result.nextRecordOffset()).toShort()
        return (result as Compact)/*.sourceRow*/
    }

    private fun findNext() {
        while (currentPage.pageHeader.recordCount.toInt() == 0) {
            if (currentPage.fileHeader.next == 0) {
                //  empty root page node
                return
            }
            currentPage = findPageByOffset(currentPage.fileHeader.next, currentPage.ext.belongIndex)
        }
        if (nextOffset == ConstantSize.INFIMUM.offset().toShort()) {
            val row = currentPage.getFirstUserRecord()
            currentRow = row
            nextOffset = row.absoluteOffset().toShort()
            return
        }
        if (nextOffset == ConstantSize.SUPREMUM.offset().toShort()) {
            if (currentPage.fileHeader.next == 0) {
                return
            }
            currentPage = findPageByOffset(currentPage.fileHeader.next, currentPage.ext.belongIndex)
            nextOffset = ConstantSize.INFIMUM.offset().toShort()
            return findNext()
        }
        val nextRow = currentPage.getUserRecordByOffset(nextOffset.toInt())
        currentRow = nextRow
        nextOffset = (nextRow.absoluteOffset() + nextRow.nextRecordOffset()).toShort()
    }
}
