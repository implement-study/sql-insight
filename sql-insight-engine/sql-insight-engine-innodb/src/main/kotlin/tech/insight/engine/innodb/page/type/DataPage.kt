package tech.insight.engine.innodb.page.type

import tech.insight.engine.innodb.page.InnoDbPage
import tech.insight.engine.innodb.page.InnodbUserRecord


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class DataPage(override val page: InnoDbPage) : PageType {

    override val value: Short = FIL_PAGE_INDEX_VALUE

    override fun doInsertData(data: InnodbUserRecord) {
        TODO("Not yet implemented")
    }

    override fun findPreAndNext(userRecord: InnodbUserRecord): Pair<InnodbUserRecord, InnodbUserRecord> {
        TODO("Not yet implemented")
    }


    /**
     * This page should be returned whether it is a root page or a downward call from the parent index page
     */
    override fun locatePage(userRecord: InnodbUserRecord): InnoDbPage {
        return page
    }


    companion object {
        const val FIL_PAGE_INDEX_VALUE = 0X45bf.toShort()
    }

}
