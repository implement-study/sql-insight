package tech.insight.engine.innodb.core.buffer

import tech.insight.engine.innodb.index.InnodbIndex
import tech.insight.engine.innodb.page.InnoDbPage


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
interface PageBuffer {


    /**
     * supply page use by page offset and innodb index
     */
    fun getPageAndCache(pageOffset: Int, index: InnodbIndex): InnoDbPage


    /**
     *
     * get root page of index
     *
     */
    fun getRoot(index: InnodbIndex): InnoDbPage {
        return getPageAndCache(0, index)
    }

    /**
     * current user size
     */
    fun currentSize(): Int


}
