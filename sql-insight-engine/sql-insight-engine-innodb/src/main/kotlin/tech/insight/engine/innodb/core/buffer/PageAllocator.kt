package tech.insight.engine.innodb.core.buffer

import tech.insight.engine.innodb.index.InnodbIndex
import tech.insight.engine.innodb.page.InnoDbPage


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
fun interface PageAllocator {

    /**
     * extension index file page
     *
     * @return empty page that have offset
     */
    fun allocatePage(index: InnodbIndex): InnoDbPage
}
