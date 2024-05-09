package tech.insight.engine.innodb.page.type

import tech.insight.engine.innodb.page.InnoDbPage


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class IndexPage(override val page: InnoDbPage) : PageType{


    override val value: Short = FIL_PAGE_INODE


    companion object{
        const val FIL_PAGE_INODE = 0x0003.toShort()
    }

}
