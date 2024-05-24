package tech.insight.engine.innodb.core.buffer

import tech.insight.core.bean.Database
import tech.insight.core.logging.Logging
import tech.insight.engine.innodb.index.InnodbIndex
import tech.insight.engine.innodb.page.InnoDbPage


/**
 *
 * Innodb Buffer pool , contain some page
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
object BufferPool : Logging(), PageBuffer, PageAllocator {

    private val databaseBuffers = mutableMapOf<Database, DatabaseBuffer>()


    override fun getPageAndCache(pageOffset: Int, index: InnodbIndex): InnoDbPage {
        val databaseBuffer = databaseBuffers.computeIfAbsent(index.table.database) {
            DatabaseBuffer(it)
        }
        return databaseBuffer.getPageAndCache(pageOffset, index)
    }

    override fun currentSize(): Int {
        return databaseBuffers.values.sumOf { it.currentSize() }
    }

    override fun allocatePage(index: InnodbIndex): InnoDbPage {
        val databaseBuffer = databaseBuffers.computeIfAbsent(index.table.database) {
            DatabaseBuffer(it)
        }
        return databaseBuffer.allocatePage(index)
    }


}
