package tech.insight.engine.innodb.page.type

import tech.insight.engine.innodb.page.IndexNode
import tech.insight.engine.innodb.page.InnoDbPage
import tech.insight.engine.innodb.page.InnodbUserRecord
import tech.insight.engine.innodb.page.compact.IndexRecord
import tech.insight.engine.innodb.page.compact.RowFormatFactory
import tech.insight.engine.innodb.utils.RowComparator


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class DataPage(override val page: InnoDbPage) : PageType {

    override val value: Short = FIL_PAGE_INDEX_VALUE


    /**
     * This page should be returned whether it is a root page or a downward call from the parent index page
     */
    override fun locatePage(userRecord: InnodbUserRecord): InnoDbPage {
        return page
    }

    override fun convertUserRecord(offsetInPage: Int): InnodbUserRecord {
        return RowFormatFactory.readRecordInPage(page, offsetInPage, page.ext.belongIndex.belongTo())
    }

    override fun pageIndex(): IndexRecord {
        val firstData = page.getUserRecordByOffset(page.infimum.offset() + page.infimum.nextRecordOffset())
        val columns = page.ext.belongIndex.columns()
        val values = columns.map { it.name }.map { firstData.getValueByColumnName(it) }.toTypedArray()
        return IndexRecord(IndexNode(values, page.fileHeader.offset), page.ext.belongIndex)
    }

    override fun compare(o1: InnodbUserRecord, o2: InnodbUserRecord): Int {
        return RowComparator.primaryKeyComparator().compare(o1, o2)
    }

    companion object {
        const val FIL_PAGE_INDEX_VALUE = 0X45bf.toShort()

    }

}
