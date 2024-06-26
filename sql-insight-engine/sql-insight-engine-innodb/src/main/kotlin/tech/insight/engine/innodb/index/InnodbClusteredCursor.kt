package tech.insight.engine.innodb.index

import tech.insight.core.annotation.Temporary
import tech.insight.core.bean.Cursor
import tech.insight.core.bean.Row
import tech.insight.core.bean.Where
import tech.insight.core.logging.Logging
import tech.insight.core.plan.ExplainType
import tech.insight.engine.innodb.index.scan.ScannerCursor


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class InnodbClusteredCursor(index: ClusteredIndex, private val where: Where, explainType: ExplainType) : Logging(),
    Cursor {

    val root = index.rootPage

    private val scanCursor: ScannerCursor = ScannerCursor.create(index, where, explainType)

    private var nextRow: Row? = null

    override fun close() {
        debug { "close the cursor " }
    }

    @Temporary("all scan")
    override fun hasNext(): Boolean {
        if (nextRow != null) {
            return true
        }
        findNext()
        return nextRow != null
    }

    override fun next(): Row {
        if (nextRow != null) {
            val result = nextRow!!
            nextRow = null
            return result
        }
        findNext()
        if (nextRow == null) {
            throw NoSuchElementException("no next row")
        }
        val result = nextRow!!
        nextRow = null
        return result
    }

    private fun findNext() {
        while (scanCursor.hasNext()) {
            val next = scanCursor.next()
            if (where.getBooleanValue(next)) {
                nextRow = next
                return
            }
        }
    }


}
