package tech.insight.engine.innodb.page

import java.nio.ByteBuffer
import java.util.*
import org.gongxuanzhang.easybyte.core.ByteWrapper
import org.gongxuanzhang.easybyte.core.DynamicByteBuffer
import tech.insight.core.annotation.Temporary
import tech.insight.core.logging.Logging
import tech.insight.engine.innodb.core.InnodbSessionContext
import tech.insight.engine.innodb.core.buffer.BufferPool
import tech.insight.engine.innodb.index.InnodbIndex
import tech.insight.engine.innodb.page.type.PageType


/**
 * InnoDb Page
 * size default 16K.
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 */
class InnoDbPage(index: InnodbIndex) : Logging(), ByteWrapper,
    PageObject, Iterable<InnodbUserRecord> {


    /**
     * 38 bytes
     */
    var fileHeader: FileHeader = FileHeader.create(this)
        set(value) {
            field = value
            ext.change = true
        }

    /**
     * 56 bytes
     */
    var pageHeader: PageHeader = PageHeader.create(this)
        set(value) {
            field = value
            ext.change = true
        }

    /**
     * 13 bytes
     */
    var infimum: Infimum = Infimum.create(this)
        set(value) {
            field = value
            ext.change = true
        }

    /**
     * 13 bytes
     */
    var supremum: Supremum = Supremum.create(this)
        set(value) {
            field = value
            ext.change = true
        }

    /**
     * uncertain bytes.
     * user records bytes + freeSpace = page size - other fixed size
     */
    var userRecords: UserRecords = UserRecords.create(this)
        set(value) {
            field = value
            ext.change = true
        }

    /**
     * uncertain bytes.
     */
    var pageDirectory: PageDirectory = PageDirectory.create(this)
        set(value) {
            field = value
            ext.change = true
        }

    /**
     * 8 bytes
     */
    var fileTrailer: FileTrailer = FileTrailer.create(this)
        set(value) {
            field = value
            ext.change = true
        }

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

    fun pageType(): PageType {
        val currentType = ext.pageType
        if (currentType == null || currentType.value != fileHeader.pageType) {
            ext.pageType = PageType.valueOf(fileHeader.pageType.toInt(), this)
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
     */
    private fun findPreAndNext(userRecord: InnodbUserRecord): Pair<InnodbUserRecord, InnodbUserRecord> {
        val targetSlot = findTargetSlot(userRecord)
        var pre = getUserRecordByOffset(pageDirectory.indexSlot(targetSlot + 1).toInt())
        var next = getUserRecordByOffset(pre.nextRecordOffset() + pre.absoluteOffset())
        while (this.pageType().compare(userRecord, next) > 0) {
            pre = next
            next = getUserRecordByOffset(pre.nextRecordOffset() + pre.absoluteOffset())
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
        var right = pageDirectory.slotCount() - 1
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
            if(deletedRow.recordHeader.nOwned == 0){
                pageDirectory.removeSlot(targetSlot)
            }else{
                preRecord.recordHeader.nOwned = deletedRow.recordHeader.nOwned - 1
                pageDirectory.slots[targetSlot] = preRecord.absoluteOffset().toShort()
            }
        }
        maxSlotRecord.recordHeader.nOwned--
        val nextAbsoluteOffset = deletedRow.nextRecordOffset() + deletedRow.absoluteOffset()
        preRecord.recordHeader.nextRecordOffset = (nextAbsoluteOffset - preRecord.absoluteOffset()).toShort()
        //  todo Link the deleted row to the deleted linked list.
        refreshRecordHeader(preRecord)
        refreshRecordHeader(deletedRow)
        refreshRecordHeader(maxSlotRecord)
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
        val pageUserRecord: MutableList<InnodbUserRecord> = ArrayList(
            pageHeader.recordCount + 1
        )
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
        fillInnodbUserRecords(remainRecords, this)
        val otherPage = BufferPool.allocatePage(this.ext.belongIndex)
        val otherRecords = pageUserRecord.subList(middleIndex, pageUserRecord.size)
        fillInnodbUserRecords(otherRecords, otherPage)
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
        fillInnodbUserRecords(leftRecords, leftPage)
        val rightPage = BufferPool.allocatePage(index)
        fillInnodbUserRecords(rightRecords, rightPage)
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
        if (this.pageHeader.recordCount.toInt() == 0) {
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

    private fun linkedAndAdjust(pre: InnodbUserRecord, insertRecord: InnodbUserRecord, next: InnodbUserRecord) {
        insertRecord.apply {
            setAbsoluteOffset(pageHeader.heapTop + insertRecord.beforeSplitOffset())
            this.recordHeader.heapNo = pageHeader.absoluteRecordCount.toUInt()
            this.recordHeader.nextRecordOffset = (next.absoluteOffset() - insertRecord.absoluteOffset()).toShort()
        }
        pre.recordHeader.nextRecordOffset = (insertRecord.absoluteOffset() - pre.absoluteOffset()).toShort()
        refreshRecordHeader(pre)

        //  adjust page
        userRecords.addRecord(insertRecord)
        pageHeader.absoluteRecordCount++
        pageHeader.recordCount++
        pageHeader.heapTop = (pageHeader.heapTop + insertRecord.length()).toShort()
        pageHeader.lastInsertOffset = insertRecord.absoluteOffset().toShort()
        var groupMax = next
        //  adjust group
        while (groupMax.recordHeader.nOwned == 0) {
            groupMax = getUserRecordByOffset(groupMax.absoluteOffset() + groupMax.nextRecordOffset())
        }
        val groupMaxHeader = groupMax.recordHeader
        groupMaxHeader.nOwned += 1
        if (groupMax.recordHeader.nOwned <= Constant.SLOT_MAX_COUNT) {
            return refreshRecordHeader(next)
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
        this.refreshRecordHeader(preMaxRecord)
        groupMax.recordHeader.nOwned = rightGroupCount
        this.refreshRecordHeader(groupMax)
        this.pageHeader.slotCount = (this.pageHeader.slotCount.toInt() + 1).toShort()
        pageDirectory.split(nextGroupIndex, preMaxRecord.absoluteOffset().toShort())
    }

    /**
     * you should rewrote to page when you update user record that resolve by [getUserRecordByOffset]
     */
    fun refreshRecordHeader(record: InnodbUserRecord) {
        if (record is Infimum) {
            return
        }
        if (record is Supremum) {
            return
        }
        val headerByte = record.recordHeader.toBytes()
        val bodyOffset: Int =
            record.absoluteOffset() - ConstantSize.RECORD_HEADER.size() - ConstantSize.PAGE_HEADER.size() -
                    ConstantSize.FILE_HEADER.size() -
                    ConstantSize.SUPREMUM.size() -
                    ConstantSize.INFIMUM.size()
        System.arraycopy(headerByte, 0, userRecords.body, bodyOffset, headerByte.size)
    }

    override fun length(): Int {
        return ConstantSize.PAGE.size()
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

        fun fillInnodbUserRecords(recordList: List<InnodbUserRecord>, page: InnoDbPage) {
            page.supremum = Supremum.create(page)
            page.infimum = Infimum.create(page)
            val pageHeader = PageHeader.create(page).apply {
                this.slotCount = ((recordList.size / Constant.SLOT_MAX_COUNT) + 2).toShort()
                this.absoluteRecordCount = (2 + recordList.size).toShort()
                this.recordCount = recordList.size.toShort()
            }
            page.pageHeader = pageHeader
            val slots = ShortArray(pageHeader.slotCount.toInt())
            slots[0] = ConstantSize.SUPREMUM.offset().toShort()
            slots[slots.size - 1] = ConstantSize.INFIMUM.offset().toShort()
            page.pageDirectory = PageDirectory.wrap(slots, page)
            var pre: InnodbUserRecord = page.infimum
            var preOffset: Short = ConstantSize.INFIMUM.offset().toShort()
            for (i in recordList.indices) {
                val current: InnodbUserRecord = recordList[i]
                val currentOffset: Int = pageHeader.lastInsertOffset + current.beforeSplitOffset()
                current.setAbsoluteOffset(currentOffset)
                pageHeader.lastInsertOffset = (pageHeader.lastInsertOffset + current.length()).toShort()
                pageHeader.heapTop = pageHeader.lastInsertOffset
                pre.recordHeader.nextRecordOffset = (currentOffset - preOffset).toShort()
                pre = current
                preOffset = currentOffset.toShort()
                if ((i + 1) % Constant.SLOT_MAX_COUNT == 0) {
                    slots[slots.size - 1 - ((i + 1) / Constant.SLOT_MAX_COUNT)] = currentOffset.toShort()
                }
            }
            pre.recordHeader.nextRecordOffset = (ConstantSize.SUPREMUM.offset() - pre.absoluteOffset()).toShort()
            page.userRecords = UserRecords.create(page).apply { addRecords(recordList) }
            page.supremum.recordHeader.nOwned = (recordList.size and (Constant.SLOT_MAX_COUNT - 1)) + 1
            page.fileTrailer = FileTrailer.create(page)
        }

        fun createRootPage(index: InnodbIndex) = InnoDbPage(index)

        /**
         * swap byte array to page
         */
        fun swap(bytes: ByteArray, index: InnodbIndex): InnoDbPage {
            ConstantSize.PAGE.checkSize(bytes)
            val page = InnoDbPage(index)
            val buffer: DynamicByteBuffer = DynamicByteBuffer.wrap(bytes)
            //  file header
            val fileHeaderBytes: ByteArray = buffer.getLength(ConstantSize.FILE_HEADER.size())
            val fileHeader = FileHeader.wrap(fileHeaderBytes, page)
            return page.apply {
                page.fileHeader = fileHeader
                val pageHeaderBytes = buffer.getLength(ConstantSize.PAGE_HEADER.size())
                this.pageHeader = PageHeader.wrap(pageHeaderBytes, this)
            }.apply {
                val infimumBytes = buffer.getLength(ConstantSize.INFIMUM.size())
                this.infimum = Infimum.wrap(infimumBytes, page)
                val supremumBytes: ByteArray = buffer.getLength(ConstantSize.SUPREMUM.size())
                this.supremum = Supremum.wrap(supremumBytes, page)
            }.apply {
                var dirOffset: Int = bytes.size - ConstantSize.FILE_TRAILER.size() - Short.SIZE_BYTES
                val byteBuffer = ByteBuffer.wrap(bytes)
                val shortList: MutableList<Short> = ArrayList()
                var slot = byteBuffer.getShort(dirOffset)
                while (slot.toInt() != 0) {
                    shortList.add(slot)
                    dirOffset -= Short.SIZE_BYTES
                    slot = byteBuffer.getShort(dirOffset)
                }
                val slots = ShortArray(shortList.size)
                for (i in slots.indices) {
                    slots[i] = shortList[shortList.size - 1 - i]
                }
                this.pageDirectory = PageDirectory.wrap(slots, this)
            }.apply {
                val bodyLength: Int = pageHeader.heapTop - PageHeader.EMPTY_PAGE_HEAP_TOP
                val body = Arrays.copyOfRange(
                    bytes, PageHeader.EMPTY_PAGE_HEAP_TOP.toInt(),
                    PageHeader.EMPTY_PAGE_HEAP_TOP + bodyLength
                )
                val userRecords = UserRecords.wrap(body, this)
                this.userRecords = userRecords
            }.apply {
                val trailerArr = Arrays.copyOfRange(bytes, bytes.size - ConstantSize.FILE_TRAILER.size(), bytes.size)
                this.fileTrailer = FileTrailer.wrap(trailerArr, this)
            }
        }

        @Temporary("direct get from buffer pool")
        fun findPageByOffset(pageOffset: Int, index: InnodbIndex): InnoDbPage {
            return BufferPool.getPageAndCache(pageOffset, index)
        }


    }
}
