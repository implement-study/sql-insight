package tech.insight.engine.innodb.index.scan

import java.nio.ByteBuffer
import tech.insight.core.bean.Row
import tech.insight.core.plan.ExplainType
import tech.insight.engine.innodb.index.InnodbIndex
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
    private var nextOffset: Int = 0

    private var currentRow: InnodbUserRecord? = null

    init {
        var page = index.rootPage
        while (page.pageType() !is DataPage) {
            val firstUserRecord = page.getFirstUserRecord()
            val offset = ByteBuffer.wrap((firstUserRecord as Compact).point()).getInt()
            page = findPageByOffset(offset, page.ext.belongIndex)
        }
        this.currentPage = page
        this.nextOffset = page.infimum.offsetInPage()
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
            nextOffset = result.offsetInPage() + result.nextRecordOffset()
            return (result as Compact)
        }
        findNext()
        if (currentRow == null) {
            throw NoSuchElementException("no next row")
        }
        val result = currentRow!!
        currentRow = null
        nextOffset = result.offsetInPage() + result.nextRecordOffset()
        return (result as Compact)
    }

    private fun findNext() {
        while (currentPage.pageHeader.recordCount == 0) {
            if (currentPage.fileHeader.next == 0) {
                //  empty root page node
                return
            }
            currentPage = findPageByOffset(currentPage.fileHeader.next, currentPage.ext.belongIndex)
        }
        if (nextOffset == currentPage.infimum.offsetInPage()) {
            val row = currentPage.getFirstUserRecord()
            currentRow = row
            nextOffset = row.offsetInPage()
            return
        }
        if (nextOffset == currentPage.supremum.offsetInPage()) {
            if (currentPage.fileHeader.next == 0) {
                return
            }
            currentPage = findPageByOffset(currentPage.fileHeader.next, currentPage.ext.belongIndex)
            nextOffset = currentPage.infimum.offsetInPage()
            return findNext()
        }
        val nextRow = currentPage.getUserRecordByOffset(nextOffset)
        currentRow = nextRow
        nextOffset = nextRow.offsetInPage() + nextRow.nextRecordOffset()
    }
}
