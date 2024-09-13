package tech.insight.engine.innodb.page

import io.netty.buffer.ByteBuf
import tech.insight.core.annotation.Temporary
import tech.insight.core.bean.condition.Expression
import tech.insight.core.logging.Logging
import tech.insight.engine.innodb.core.InnodbSessionContext
import tech.insight.engine.innodb.core.buffer.BufferPool
import tech.insight.engine.innodb.index.InnodbIndex
import tech.insight.engine.innodb.page.compact.Compact
import tech.insight.engine.innodb.page.compact.RecordHeader
import tech.insight.engine.innodb.page.compact.RowFormatFactory
import tech.insight.engine.innodb.page.type.PageType


/**
 * InnoDb Page
 * source is 16K bytes
 * @author gxz gongxuanzhangmelt@gmail.com
 */
class InnoDbPage(internal val source: ByteBuf, index: InnodbIndex) : Logging(), PageObject,
    Iterable<InnodbUserRecord>, Comparator<InnodbUserRecord> {

    init {
        require(source.capacity() == ConstantSize.PAGE.size) {
            "page size must be 16K"
        }
    }

    /**
     * 38 bytes
     */
    val fileHeader = FileHeader(this)

    /**
     * 56 bytes
     */
    val pageHeader = PageHeader(this)

    /**
     * 13 bytes
     */
    val infimum = Infimum(this)

    /**
     * 13 bytes
     */
    val supremum = Supremum(this)

    /**
     * uncertain bytes.
     * user records bytes + freeSpace = page size - other fixed size
     */
    val userRecords = UserRecords(this)

    /**
     * uncertain bytes.
     */
    val pageDirectory = PageDirectory(this)

    /**
     * 8 bytes
     */
    val fileTrailer = FileTrailer(this)

    /**
     * some extra info for page
     */
    var ext: PageExt

    init {
        ext = PageExt()
        ext.belongIndex = index
    }

    override fun toBytes(): ByteArray {
        return source.array()
    }

    /**
     * the page free space length
     */
    fun remainSpace() = ConstantSize.FILE_TRAILER.offset - pageHeader.slotCount * Short.SIZE_BYTES - pageHeader.heapTop

    fun pageType(): PageType {
        val currentType = ext.pageType
        if (currentType == null || currentType.value != fileHeader.pageType) {
            ext.pageType = PageType.valueOf(fileHeader.pageType, this)
        }
        return ext.pageType!!
    }

    val freeSpace: UShort
        get() = (ConstantSize.PAGE.size -
                ConstantSize.PAGE_HEADER.size -
                ConstantSize.FILE_HEADER.size -
                ConstantSize.FILE_TRAILER.size -
                ConstantSize.SUPREMUM.size -
                ConstantSize.INFIMUM.size -
                pageDirectory.length() - userRecords.length()).toUShort()

    /**
     * add a data.
     * data means a insert row .
     * page is leaf node will insert data.
     * page is index node will find target leaf node and insert data.
     * may be split page in process
     */
    fun insertData(data: InnodbUserRecord) {
        val targetPage = this.locatePage(data)
        targetPage.doInsertData(data)
    }

    /**
     * link [PageType.locatePage]
     */
    fun locatePage(data: InnodbUserRecord): InnoDbPage {
        return this.pageType().locatePage(data)
    }


    /**
     *
     */
    private fun doInsertData(data: InnodbUserRecord) {
        InnodbSessionContext.getInnodbSessionContext().modifyPage(this)
        var preCandidate = pageDirectory.findTargetIn(data).minRecord()
        var nextCandidate = preCandidate.nextRecord()
        //   <= ? <  this data already existed
        while (nextCandidate <= data) {
            preCandidate = nextCandidate
            nextCandidate = preCandidate.nextRecord()
        }
        val preFree = this.freeSpace
        linkedAndAdjust(preCandidate, data, nextCandidate)
        val after = this.freeSpace
        val diff = preFree - after
        debug { "data: ${data.toBytes().size} diff: $diff pre: $preFree after: $after " }
        pageSplitIfNecessary()
    }


    /**
     * data page will split when free space less than one in thirty-two page size
     */
    private fun pageSplitIfNecessary() {
        if (freeSpace.toInt() > ConstantSize.PAGE.size shr 4) {
            return
        }
        val pageUserRecord: MutableList<InnodbUserRecord> = ArrayList(pageHeader.recordCount + 1)
        var base: InnodbUserRecord = infimum
        var allLength = 0
        while (true) {
            base = base.nextRecord()
            if (base === supremum) {
                break
            }
            pageUserRecord.add(base)
            allLength += base.length()
        }
        when (computeSplitStrategy()) {
            PageSplitStrategy.MIDDLE_SPLIT -> middleSplit(pageUserRecord, allLength)
            else -> TODO("other split strategy not implement")
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
        var remainLength = allLength
        val half = remainLength / 2
        val middleIndex = pageUserRecord.indexOfFirst {
            remainLength -= it.length()
            remainLength <= half
        }
        if (ext.parent == null) {
            val (leftPage, rightPage) = splitToSubPage(pageUserRecord, middleIndex, this)
            return this.pageType().rootUpgrade(leftPage, rightPage)
        }
        val remainRecords = pageUserRecord.subList(0, middleIndex)
        coverRecords(remainRecords)
        val otherPage = BufferPool.allocatePage(this.ext.belongIndex)
        val otherRecords = pageUserRecord.subList(middleIndex, pageUserRecord.size)
        otherPage.coverRecords(otherRecords)
        return this.pageType().upgrade(otherPage)
    }


    /**
     * page split create a new page.
     * page only have user records
     * @param recordList data or index in the page that sorted
     * @return first of pair is left node,second is right node
     */
    private fun splitToSubPage(
        recordList: List<InnodbUserRecord>,
        splitIndex: Int,
        parentPage: InnoDbPage
    ): Pair<InnoDbPage, InnoDbPage> {
        val index = parentPage.ext.belongIndex
        val leftRecords = recordList.subList(0, splitIndex)
        val rightRecords = recordList.subList(splitIndex, recordList.size)
        val leftPage = BufferPool.allocatePage(index)
        leftPage.coverRecords(leftRecords)
        val rightPage = BufferPool.allocatePage(index)
        rightPage.coverRecords(rightRecords)
        return Pair(leftPage, rightPage)
    }

    /**
     * At present there is only a middle split
     */
    private fun computeSplitStrategy(): PageSplitStrategy {
        if (pageHeader.directionCount < Constant.DIRECTION_COUNT_THRESHOLD) {
            return PageSplitStrategy.MIDDLE_SPLIT
        }
        return PageSplitStrategy.NOT_SPLIT
    }

    /**
     * @param offsetInPage offset in page, offset is after record header , aka    |vars|null|record header| here!| record body|
     * @return user record
     */
    fun getUserRecordByOffset(offsetInPage: Int): InnodbUserRecord {
        if (offsetInPage == this.infimum.absoluteOffset()) {
            return infimum
        }
        if (offsetInPage == this.supremum.absoluteOffset()) {
            return supremum
        }
        return this.pageType().convertUserRecord(offsetInPage)
    }


    /**
     * get the first user record in page user records linked.
     */
    fun getFirstUserRecord(): InnodbUserRecord {
        if (this.pageHeader.recordCount == 0) {
            throw NoSuchElementException("page is empty")
        }
        return getUserRecordByOffset(infimum.absoluteOffset() + infimum.nextRecordOffset())
    }

    /**
     * @param offset record offset, the record header offset = offset - record header size
     * @return record
     */
    fun readRecordHeader(offset: Int): RecordHeader {
        val recordHeaderSize: Int = ConstantSize.RECORD_HEADER.size
        return RecordHeader(source.slice(offset - recordHeaderSize, recordHeaderSize))
    }

    /**
     * get the first index node to parent node insert
     * link [PageType.pageIndex]
     */
    fun pageIndex(): InnodbUserRecord {
        return pageType().pageIndex()
    }


    fun update(oldRow: Compact, updateFields: Map<String, Expression>) {
        InnodbSessionContext.getInnodbSessionContext().modifyPage(this)
        val updateCompact = RowFormatFactory.compactFromUpdateRow(oldRow, updateFields)
        return replace(oldRow, updateCompact)
    }

    private fun linkedAndAdjust(pre: InnodbUserRecord, insertRecord: InnodbUserRecord, next: InnodbUserRecord) {
        val recordInPage = userRecords.addRecord(insertRecord)
        pre.linkRecord(recordInPage)
        recordInPage.linkRecord(next)
        val groupMax = next.groupMax()
        if (++groupMax.recordHeader.nOwned <= Constant.SLOT_MAX_COUNT) {
            return
        }
        debug { "occurred group split ..." }
        val splitSlot = pageDirectory.requireSlotByOffset(groupMax.absoluteOffset())
        //   todo group split strategy
        val leftGroupCount = Constant.SLOT_MAX_COUNT shr 1
        val leftMaxRecord = run {
            var candidate = splitSlot.smaller().maxRecord()
            repeat(leftGroupCount) {
                candidate = candidate.nextRecord()
            }
            candidate.apply {
                recordHeader.nOwned = leftGroupCount
            }
        }
        groupMax.recordHeader.nOwned = Constant.SLOT_MAX_COUNT - leftGroupCount + 1
        pageDirectory.insert(splitSlot.index, leftMaxRecord.absoluteOffset())
    }


    fun coverRecords(records: List<InnodbUserRecord>) {
        this.clear()
        records.forEach { this.insertData(it) }
        //   todo batch insert 
    }

    /**
     * remove all user records in this page,However, it does not change the position of the page in the whole b+ tree.
     * in other words file header and file trailer is not changed.
     */
    internal fun clear() {
        this.userRecords.clear()
    }

    override fun length(): Int {
        return ConstantSize.PAGE.size
    }

    override val belongPage: InnoDbPage = this

    override fun iterator(): Iterator<InnodbUserRecord> {
        return Itr()
    }

    override fun hashCode(): Int {
        return fileHeader.spaceId + 31 * fileHeader.offset
    }

    override fun compare(o1: InnodbUserRecord, o2: InnodbUserRecord): Int {
        return this.pageType().compare(o1, o2)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InnoDbPage) return false

        if (fileHeader != other.fileHeader) return false
        return true
    }


    inner class Itr : Iterator<InnodbUserRecord> {
        private var cursor = getUserRecordByOffset(infimum.nextRecordOffset() + infimum.absoluteOffset())
        override fun hasNext(): Boolean {
            return cursor !== supremum
        }

        override fun next(): InnodbUserRecord {
            if (cursor === supremum) {
                throw NoSuchElementException()
            }
            val result = cursor
            cursor = getUserRecordByOffset(cursor.nextRecordOffset() + cursor.absoluteOffset())
            return result
        }

    }

    inner class PageExt {

        lateinit var belongIndex: InnodbIndex

        var parent: InnoDbPage? = null

        var pageType: PageType? = null

        var change: Boolean = true

        var bytes: ByteArray = ByteArray(0)


        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is PageExt) return false

            if (belongIndex != other.belongIndex) return false
            if (parent != other.parent) return false

            return true
        }

        override fun hashCode(): Int {
            var result = belongIndex.hashCode()
            result = 31 * result + (parent?.hashCode() ?: 0)
            return result
        }

    }


    companion object {


        @Temporary("direct get from buffer pool")
        fun findPageByOffset(pageOffset: Int, index: InnodbIndex): InnoDbPage {
            return BufferPool.getPageAndCache(pageOffset, index)
        }


    }
}
