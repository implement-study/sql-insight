package tech.insight.engine.innodb.page.type

import tech.insight.engine.innodb.index.InnodbIndex
import tech.insight.engine.innodb.page.*
import tech.insight.engine.innodb.page.compact.IndexRecord
import tech.insight.engine.innodb.page.compact.RowFormatFactory
import tech.insight.engine.innodb.utils.RowComparator


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class DataPage(override val page: InnoDbPage) : PageType {

    override val value: Short = FIL_PAGE_INDEX_VALUE

    override fun doInsertData(data: InnodbUserRecord) {
        TODO("Not yet implemented")
    }

    /**
     * data page will split when free space less than one in thirty-two page size
     */
    override fun pageSplitIfNecessary() {
        if (page.freeSpace.toInt() > ConstantSize.PAGE.size() shr 4) {
            return
        }
        val pageUserRecord: MutableList<InnodbUserRecord> = ArrayList(
            page.pageHeader.recordCount + 1
        )
        var base: InnodbUserRecord = page.infimum
        var allLength = 0
        while (true) {
            base = page.getUserRecordByOffset(base.offset() + base.nextRecordOffset())
            if (base === page.supremum) {
                break
            }
            pageUserRecord.add(base)
            allLength += base.length()
        }
        //   todo if not middle split ?
        if (page.pageHeader.directionCount < Constant.DIRECTION_COUNT_THRESHOLD) {
            middleSplit(pageUserRecord, allLength)
        }
    }


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
        TODO("Not yet implemented")
    }

    override fun compare(o1: InnodbUserRecord, o2: InnodbUserRecord): Int {
        return RowComparator.primaryKeyComparator().compare(o1, o2)
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
        for (i in pageUserRecord.indices) {
            lengthCandidate -= pageUserRecord[i].length()
            if (lengthCandidate <= half) {
                val (firstDataPage, secondDataPage) = splitToDataPage(pageUserRecord, i, this.page)
                upgrade(firstDataPage, secondDataPage)
                return
            }
        }
    }


    /**
     * page split create a new data page.
     *
     * @param recordList data in the page that sorted
     * @return first of pair is left node,second is right node
     */
    private fun splitToDataPage(
        recordList: List<InnodbUserRecord>,
        splitIndex: Int,
        parentPage: InnoDbPage
    ): Pair<InnoDbPage, InnoDbPage> {
        val index = parentPage.ext.belongIndex
        val left = createFromUserRecords(recordList.subList(0, splitIndex), index)
        val right = createFromUserRecords(recordList.subList(splitIndex, recordList.size), index)
        left.fileHeader = FileHeader.create().apply {
            this.next = 0
            this.pre = 0
            this.offset = 0
            this.pageType = FIL_PAGE_INDEX_VALUE
        }

        return Pair(left, right)
    }

    companion object {
        const val FIL_PAGE_INDEX_VALUE = 0X45bf.toShort()

        private fun createFromUserRecords(recordList: List<InnodbUserRecord>, index: InnodbIndex): InnoDbPage {
            val dataPage = InnoDbPage(index)
            fillInnodbUserRecords(recordList, dataPage)
            dataPage.fileHeader.pageType = FIL_PAGE_INDEX_VALUE
            return dataPage
        }

        private fun fillInnodbUserRecords(recordList: List<InnodbUserRecord>, page: InnoDbPage) {
            page.fileHeader = FileHeader.create()
            page.supremum = Supremum.create()
            page.infimum = Infimum.create()
            val pageHeader = PageHeader.create().apply {
                this.slotCount = ((recordList.size + 1) / 8 + 1).toShort()
                this.absoluteRecordCount = (2 + recordList.size).toShort()
                this.recordCount = recordList.size.toShort()
                this.lastInsertOffset = ConstantSize.USER_RECORDS.offset().toShort()
            }
            page.pageHeader = pageHeader
            val slots = ShortArray((recordList.size + 1) / Constant.SLOT_MAX_COUNT + 1)
            slots[0] = ConstantSize.SUPREMUM.offset().toShort()
            slots[slots.size - 1] = ConstantSize.INFIMUM.offset().toShort()
            page.pageDirectory = PageDirectory(slots)
            page.userRecords = UserRecords().apply { addRecords(recordList) }
            var pre: InnodbUserRecord = page.infimum
            val preOffset: Short = ConstantSize.SUPREMUM.offset().toShort()
            for (i in recordList.indices) {
                val current: InnodbUserRecord = recordList[i]
                val currentOffset: Int = pageHeader.lastInsertOffset + current.beforeSplitOffset()
                pageHeader.lastInsertOffset = (pageHeader.lastInsertOffset + current.length()).toShort()
                pre.recordHeader.setNextRecordOffset(currentOffset - preOffset)
                pre = current
                if ((i + 1) % Constant.SLOT_MAX_COUNT == 0) {
                    slots[slots.size - 1 - (i + 1) % Constant.SLOT_MAX_COUNT] = currentOffset.toShort()
                }
            }
            pre.recordHeader.setNextRecordOffset(ConstantSize.SUPREMUM.offset())
            page.fileTrailer = FileTrailer.create()
        }

    }

}
