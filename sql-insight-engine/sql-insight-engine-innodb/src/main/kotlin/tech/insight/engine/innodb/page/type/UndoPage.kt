package tech.insight.engine.innodb.page.type

import tech.insight.engine.innodb.page.InnoDbPage


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class UndoPage(override val page: InnoDbPage) : PageType {

    override val value: Short = FIL_PAGE_TYPE_UNDO_LOG


    companion object {
        const val FIL_PAGE_TYPE_UNDO_LOG = 0X0001.toShort()
    }
}
