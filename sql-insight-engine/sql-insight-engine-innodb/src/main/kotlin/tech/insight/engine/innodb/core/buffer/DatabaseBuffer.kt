package tech.insight.engine.innodb.core.buffer

import tech.insight.core.bean.Database
import tech.insight.engine.innodb.index.InnodbIndex
import tech.insight.engine.innodb.page.InnoDbPage


/**
 * @author gongxuanzhangmelt@gmail.com
 */
class DatabaseBuffer(val database: Database) : PageBuffer, PageAllocator {

    private val tableBuffers = mutableMapOf<String, TableBuffer>()

    override fun getPageAndCache(pageOffset: Int, index: InnodbIndex): InnoDbPage {
        val tableBuffer = tableBuffers.computeIfAbsent(index.table.name) {
            TableBuffer(index.table)
        }
        return tableBuffer.getPageAndCache(pageOffset, index)
    }

    override fun currentSize(): Int {
        return tableBuffers.values.sumOf { it.currentSize() }
    }

    override fun allocatePage(index: InnodbIndex): InnoDbPage {
        val tableBuffer = tableBuffers.computeIfAbsent(index.table.name) {
            TableBuffer(index.table)
        }
        return tableBuffer.allocatePage(index)
    }


}
