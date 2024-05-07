package tech.insight.engine.innodb.page

import tech.insight.core.bean.Column
import tech.insight.engine.innodb.index.InnodbIndex
import tech.insight.engine.innodb.page.compact.IndexRecord
import tech.insight.engine.innodb.page.compact.RowFormatFactory
import tech.insight.engine.innodb.utils.RowComparator


/**
 * @author gongxuanzhangmelt@gmail.com
 */
class DataPage(index: InnodbIndex) : InnoDbPage(index) {


    override fun wrapUserRecord(offsetInPage: Int): InnodbUserRecord {
        return RowFormatFactory.readRecordInPage(this, offsetInPage, ext.belongIndex.belongTo())
    }

    /**
     * data page will split when free space less than one-sixteenth page size
     */
    override fun pageSplitIfNecessary() {
        if (this.freeSpace > ConstantSize.PAGE.size() shr 4) {
            return
        }
        val pageUserRecord: MutableList<InnodbUserRecord> = ArrayList(
            pageHeader.recordCount + 1
        )
        var base: InnodbUserRecord = infimum
        var allLength = 0
        while (true) {
            base = getUserRecordByOffset(base.offset() + base.nextRecordOffset())
            if (base === supremum) {
                break
            }
            pageUserRecord.add(base)
            allLength += base.length()
        }
        //   todo non middle split ?
        if (pageHeader.directionCount < Constant.DIRECTION_COUNT_THRESHOLD) {
            middleSplit(pageUserRecord, allLength)
        }
    }

    /**
     * middle split.
     * insert direction unidentified (directionCount less than 5)
     *
     *
     * if this page is root page.
     * transfer root page to index page from data page.
     * create two data page linked.
     * if this page is normal leaf node,
     * create a data page append to index file and insert a index record to parent (index page)
     *
     * @param pageUserRecord all user record in page with inserted
     * @param allLength      all user record length
     */
    private fun middleSplit(pageUserRecord: List<InnodbUserRecord>, allLength: Int) {
        var lengthCandidate = allLength
        val half = lengthCandidate / 2
        var firstDataPage: DataPage? = null
        var secondDataPage: DataPage? = null
        for (i in pageUserRecord.indices) {
            lengthCandidate -= pageUserRecord[i].length()
            if (lengthCandidate <= half) {
                val belong = ext.belongIndex
                firstDataPage = createDataPage(pageUserRecord.subList(0, i), belong)
                secondDataPage = createDataPage(pageUserRecord.subList(i, pageUserRecord.size), belong)
                break
            }
        }
        if (firstDataPage == null) {
            throw NullPointerException("data page error")
        }
        upgrade(firstDataPage, secondDataPage!!)
    }

    override fun pageIndex(): IndexRecord {
        val firstData = getUserRecordByOffset(infimum.offset() + infimum.nextRecordOffset())
        val columns: List<Column> = ext.belongIndex.columns()
        val values = columns.map { it.name }.map { firstData.getValueByColumnName(it) }.toTypedArray()
        return IndexRecord(IndexNode(values, fileHeader.offset), ext.belongIndex)
    }

    override fun compare(o1: InnodbUserRecord, o2: InnodbUserRecord): Int {
        return RowComparator.primaryKeyComparator().compare(o1, o2)
    }
}
