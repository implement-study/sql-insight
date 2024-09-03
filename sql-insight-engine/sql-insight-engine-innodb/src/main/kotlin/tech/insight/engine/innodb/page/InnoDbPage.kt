package tech.insight.engine.innodb.page

import io.netty.buffer.ByteBuf
import org.gongxuanzhang.easybyte.core.ByteWrapper
import org.gongxuanzhang.easybyte.core.DynamicByteBuffer
import tech.insight.core.annotation.Temporary
import tech.insight.core.bean.condition.Expression
import tech.insight.core.logging.Logging
import tech.insight.engine.innodb.core.InnodbSessionContext
import tech.insight.engine.innodb.core.buffer.BufferPool
import tech.insight.engine.innodb.index.InnodbIndex
import tech.insight.engine.innodb.page.compact.Compact
import tech.insight.engine.innodb.page.compact.RowFormatFactory
import tech.insight.engine.innodb.page.type.PageType


/**
 * InnoDb Page
 * source is 16K bytes
 * @author gxz gongxuanzhangmelt@gmail.com
 */
class InnoDbPage(internal val source: ByteBuf, index: InnodbIndex) : Logging(), ByteWrapper, PageObject,
    Iterable<InnodbUserRecord> {

    init {
        require(source.capacity() == ConstantSize.PAGE.size()) {
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
        if (ext.change) {
            val buffer: DynamicByteBuffer = DynamicByteBuffer.allocate()
            buffer.append(fileHeader.toBytes())
            buffer.append(pageHeader.toBytes())
            buffer.append(infimum.toBytes())
            buffer.append(supremum.toBytes())
            buffer.append(userRecords.toBytes())
            buffer.append(ByteArray(freeSpace.toInt()))
            buffer.append(pageDirectory.toBytes())
            buffer.append(fileTrailer.toBytes())
            ext.bytes = buffer.toBytes()
        }
        return ext.bytes
    }

    /**
     * the page free space length
     */
    fun remainSpace() = ConstantSize.FILE_TRAILER.offset - pageHeader.slotCount * Short.SIZE_BYTES - pageHeader.heapTop

    fun pageType(): PageType {
        val currentType = ext.pageType
        if (currentType == null || currentType.value.toInt() != fileHeader.pageType) {
            ext.pageType = PageType.valueOf(fileHeader.pageType, this)
        }
        return ext.pageType!!
    }

    val freeSpace: UShort
        get() = (ConstantSize.PAGE.size() -
                ConstantSize.PAGE_HEADER.size() -
                ConstantSize.FILE_HEADER.size() -
                ConstantSize.FILE_TRAILER.size() -
                ConstantSize.SUPREMUM.size() -
                ConstantSize.INFIMUM.size() -
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
        val (pre, next) = findPreAndNext(data)
        val preFree = this.freeSpace
        linkedAndAdjust(pre, data, next)
        val after = this.freeSpace
        val diff = preFree - after
        debug { " data: ${data.toBytes().size} diff: $diff pre: $preFree after: $after " }
        pageSplitIfNecessary()
    }

    /**
     * find location where the record linked in page and return pre and next.
     * search only,don't affect the page .
     * @param userRecord target record
     * @param skipMe will throw Error when find a record equals [userRecord] and this param is true
     */
    fun findPreAndNext(
        userRecord: InnodbUserRecord,
        skipMe: Boolean = false
    ): Pair<InnodbUserRecord, InnodbUserRecord> {
        val targetSlot = findTargetSlot(userRecord)
        var pre = getUserRecordByOffset(pageDirectory.indexSlot(targetSlot + 1).toInt())
        var next = getUserRecordByOffset(pre.nextRecordOffset() + pre.absoluteOffset())
        while (true) {
            val compare = this.pageType().compare(userRecord, next)
            if (compare < 0) {
                break
            }
            if (compare > 0) {
                pre = next
                next = getUserRecordByOffset(next.nextRecordOffset() + next.absoluteOffset())
                continue
            }
            if (skipMe) {
                val myNext = getUserRecordByOffset(next.nextRecordOffset() + next.absoluteOffset())
                return Pair(pre, myNext)
            }
            throw Error("find a record equals target record")
        }
        return Pair(pre, next)
    }

    /**
     * get the first user record in  page directory slot .
     * @param targetSlot which slot in page directory
     */
    fun targetSlotMinUserRecord(targetSlot: Int): InnodbUserRecord {
        val pre = getUserRecordByOffset(pageDirectory.indexSlot(targetSlot + 1).toInt())
        return getUserRecordByOffset(pre.nextRecordOffset() + pre.absoluteOffset())
    }

    /**
     * find the slot where the target record is located
     *
     *
     * return 0 means supremum
     *
     * @return slot index is user record inserted never return `slot.length -1 ` because slot.length - 1 is the
     * infimum
     */
    fun findTargetSlot(userRecord: InnodbUserRecord): Int {
        if (pageDirectory.slots.size == 2) {
            return 0
        }
        val maxExcludeSupremum = getUserRecordByOffset(pageDirectory.slots[1].toInt())
        if (this.pageType().compare(maxExcludeSupremum, userRecord) < 0) {
            return 0
        }
        var left = 1
        var right = pageHeader.slotCount - 1
        while (left < right) {
            val mid = left + ((right - left) shr 1)
            val offset: Short = pageDirectory.slots[mid]
            val base = getUserRecordByOffset(offset.toInt())
            val compare = pageType().compare(userRecord, base)
            if (compare == 0) {
                return mid
            }
            if (compare > 0) {
                right = mid - 1
                continue
            }
            if (left == mid) {
                break
            }
            left = mid
        }
        if (pageType().compare(userRecord, getUserRecordByOffset(pageDirectory.slots[right].toInt())) <= 0) {
            return right
        }
        return left
    }

    //   todo 
    fun delete(deletedRow: InnodbUserRecord) {
        val isFirstRecord = getFirstUserRecord().absoluteOffset() == deletedRow.absoluteOffset()
        val targetSlot = findTargetSlot(deletedRow)
        val preRecord = run {
            val slotMinRecord = targetSlotMinUserRecord(targetSlot)
            if (slotMinRecord.absoluteOffset() == deletedRow.absoluteOffset()) {
                return@run getUserRecordByOffset(pageDirectory.indexSlot(targetSlot + 1).toInt())
            }
            var pre = slotMinRecord
            while (pre.nextRecordOffset() + pre.absoluteOffset() != deletedRow.absoluteOffset()) {
                pre = getUserRecordByOffset(pre.nextRecordOffset() + pre.absoluteOffset())
            }
            pre
        }
        val maxSlotRecord = getUserRecordByOffset(pageDirectory.indexSlot(targetSlot).toInt())

        if (maxSlotRecord.absoluteOffset() == deletedRow.absoluteOffset()) {
            if (deletedRow.recordHeader.nOwned == 0) {
                pageDirectory.removeSlot(targetSlot)
            } else {
                preRecord.recordHeader.nOwned = deletedRow.recordHeader.nOwned - 1
                pageDirectory.slots[targetSlot] = preRecord.absoluteOffset().toShort()
            }
        }
        maxSlotRecord.recordHeader.nOwned--
        val nextAbsoluteOffset = deletedRow.nextRecordOffset() + deletedRow.absoluteOffset()
        preRecord.recordHeader.nextRecordOffset = (nextAbsoluteOffset - preRecord.absoluteOffset())
        //  todo Link the deleted row to the deleted linked list.
        if (isFirstRecord) {
            this.ext.parent?.delete(deletedRow.indexNode())
        }
    }


    /**
     * data page will split when free space less than one in thirty-two page size
     */
    private fun pageSplitIfNecessary() {
        if (freeSpace.toInt() > ConstantSize.PAGE.size() shr 4) {
            return
        }
        val pageUserRecord: MutableList<InnodbUserRecord> = ArrayList(pageHeader.recordCount + 1)
        var base: InnodbUserRecord = infimum
        var allLength = 0
        while (true) {
            base = getUserRecordByOffset(base.absoluteOffset() + base.nextRecordOffset())
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
        if (offsetInPage == ConstantSize.INFIMUM.offset()) {
            return infimum
        }
        if (offsetInPage == ConstantSize.SUPREMUM.offset()) {
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

    fun getPreUserRecord(userRecord: InnodbUserRecord): InnodbUserRecord {
        check(userRecord !is Infimum) {
            "infimum has no pre"
        }
        val targetSlot = findTargetSlot(userRecord)
        val slotFirstRecord = targetSlotMinUserRecord(targetSlot)
        if (slotFirstRecord.absoluteOffset() == userRecord.absoluteOffset()) {
            return getUserRecordByOffset(pageDirectory.indexSlot(targetSlot + 1).toInt())
        }
        var pre = slotFirstRecord
        while (pre.nextRecordOffset() + pre.absoluteOffset() != userRecord.absoluteOffset()) {
            pre = getUserRecordByOffset(pre.nextRecordOffset() + pre.absoluteOffset())
        }
        return pre
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
        insertRecord.apply {
            setAbsoluteOffset(pageHeader.heapTop + insertRecord.beforeSplitOffset())
            this.recordHeader.heapNo = pageHeader.absoluteRecordCount.toInt()
            this.recordHeader.nextRecordOffset = (next.absoluteOffset() - insertRecord.absoluteOffset())
        }
        pre.recordHeader.nextRecordOffset = (insertRecord.absoluteOffset() - pre.absoluteOffset())

        //  adjust page
        userRecords.addRecord(insertRecord)
        pageHeader.absoluteRecordCount++
        pageHeader.recordCount++
        pageHeader.heapTop = pageHeader.heapTop + insertRecord.length()
        pageHeader.lastInsertOffset = insertRecord.absoluteOffset()
        var groupMax = next
        //  adjust group
        while (groupMax.recordHeader.nOwned == 0) {
            groupMax = getUserRecordByOffset(groupMax.absoluteOffset() + groupMax.nextRecordOffset())
        }
        val groupMaxHeader = groupMax.recordHeader
        groupMaxHeader.nOwned += 1
        if (groupMax.recordHeader.nOwned <= Constant.SLOT_MAX_COUNT) {
            return
        }
        debug { "occurred group split ..." }
        val nextGroupIndex = pageDirectory.slots.indexOfFirst { it.toInt() == groupMax.absoluteOffset() }
        var preMaxRecord = getUserRecordByOffset(pageDirectory.slots[nextGroupIndex + 1].toInt())
        val leftGroupCount = Constant.SLOT_MAX_COUNT shr 1
        val rightGroupCount = Constant.SLOT_MAX_COUNT - leftGroupCount + 1
        repeat(leftGroupCount) {
            preMaxRecord = getUserRecordByOffset(preMaxRecord.absoluteOffset() + preMaxRecord.nextRecordOffset())
        }
        preMaxRecord.recordHeader.nOwned = leftGroupCount
        groupMax.recordHeader.nOwned = rightGroupCount
        this.pageHeader.slotCount += 1
        pageDirectory.split(nextGroupIndex, preMaxRecord.absoluteOffset().toShort())
    }


    fun coverRecords(records: List<InnodbUserRecord>) {
        this.clear()
        with(pageHeader) {
            recordCount = records.size
            absoluteRecordCount = records.size + 2
            slotCount = (records.size / Constant.SLOT_MAX_COUNT) + 2
        }
        var pre: InnodbUserRecord = this.infimum
        var preOffset = ConstantSize.INFIMUM.offset()
        records.forEachIndexed { index, record ->
            val currentOffset: Int = pageHeader.heapTop + record.beforeSplitOffset()
            record.setAbsoluteOffset(currentOffset)
            pageHeader.heapTop += record.length()
            pre.recordHeader.nextRecordOffset = currentOffset - preOffset
            pre = record
            preOffset = currentOffset
            if ((index + 1) % Constant.SLOT_MAX_COUNT == 0) {
                pageDirectory.insert(pageHeader.slotCount - 2, currentOffset)
            }
        }
        pre.recordHeader.nextRecordOffset = ConstantSize.SUPREMUM.offset() - pre.absoluteOffset()
        this.userRecords.addRecords(records)
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InnoDbPage) return false

        if (fileHeader != other.fileHeader) return false
        if (pageHeader != other.pageHeader) return false
        if (infimum != other.infimum) return false
        if (supremum != other.supremum) return false
        if (userRecords != other.userRecords) return false
        if (pageDirectory != other.pageDirectory) return false
        if (fileTrailer != other.fileTrailer) return false
        if (ext != other.ext) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fileHeader.hashCode()
        result = 31 * result + pageHeader.hashCode()
        result = 31 * result + infimum.hashCode()
        result = 31 * result + supremum.hashCode()
        result = 31 * result + userRecords.hashCode()
        result = 31 * result + pageDirectory.hashCode()
        result = 31 * result + fileTrailer.hashCode()
        result = 31 * result + ext.hashCode()
        return result
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
