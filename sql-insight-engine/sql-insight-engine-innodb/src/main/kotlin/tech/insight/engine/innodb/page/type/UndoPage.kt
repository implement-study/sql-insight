package tech.insight.engine.innodb.page.type

import tech.insight.engine.innodb.page.InnoDbPage
import tech.insight.engine.innodb.page.InnodbUserRecord


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class UndoPage(override val page: InnoDbPage) : PageType {

    override val value: Short = FIL_PAGE_TYPE_UNDO_LOG
    override fun locatePage(userRecord: InnodbUserRecord): InnoDbPage {
        TODO("Not yet implemented")
    }

    override fun convertUserRecord(offsetInPage: Int): InnodbUserRecord {
        TODO("Not yet implemented")
    }

    override fun pageIndex(): InnodbUserRecord {
        TODO("Not yet implemented")
    }

    override fun rootUpgrade(leftPage: InnoDbPage, rightPage: InnoDbPage) {
        TODO("Not yet implemented")
    }

    override fun compare(o1: InnodbUserRecord?, o2: InnodbUserRecord?): Int {
        TODO("Not yet implemented")
    }


    companion object {
        const val FIL_PAGE_TYPE_UNDO_LOG = 0X0001.toShort()
    }
}
