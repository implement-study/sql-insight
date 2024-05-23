package tech.insight.engine.innodb.page

import org.gongxuanzhang.easybyte.core.ByteWrapper
import org.gongxuanzhang.easybyte.core.DynamicByteBuffer
import tech.insight.core.exception.DuplicationPrimaryKeyException
import tech.insight.core.logging.Logging
import tech.insight.engine.innodb.core.InnodbSessionContext
import tech.insight.engine.innodb.index.InnodbIndex
import tech.insight.engine.innodb.page.type.DataPage.Companion.FIL_PAGE_INDEX_VALUE
import tech.insight.engine.innodb.page.type.IndexPage.Companion.FIL_PAGE_INODE
import tech.insight.engine.innodb.page.type.PageType
import tech.insight.engine.innodb.utils.Console
import tech.insight.engine.innodb.utils.PageSupport
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.util.*


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
    lateinit var fileHeader: FileHeader

    /**
     * 56 bytes
     */
    lateinit var pageHeader: PageHeader

    /**
     * 13 bytes
     */
    lateinit var infimum: Infimum

    /**
     * 13 bytes
     */
    lateinit var supremum: Supremum

    /**
     * uncertain bytes.
     * user records bytes + freeSpace = page size - other fixed size
     */
    lateinit var userRecords: UserRecords

    /**
     * uncertain bytes.
     */
    lateinit var pageDirectory: PageDirectory

    /**
     * 8 bytes
     */
    lateinit var fileTrailer: FileTrailer

    /**
     * some extra info for page
     */
    var ext: PageExt

    init {
        ext = PageExt()
        ext.belongIndex = index
    }

    override fun toBytes(): ByteArray {
        val buffer: DynamicByteBuffer = DynamicByteBuffer.allocate()
        buffer.append(fileHeader.toBytes())
        buffer.append(pageHeader.toBytes())
        buffer.append(infimum.toBytes())
        buffer.append(supremum.toBytes())
        buffer.append(userRecords.toBytes())
        buffer.append(ByteArray(freeSpace.toInt()))
        buffer.append(pageDirectory.toBytes())
        buffer.append(fileTrailer.toBytes())
        return buffer.toBytes()
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
        Console.pageByteDescription(this)
        Console.pageCompactDescription(this)
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
        val prefree = this.freeSpace
        linkedAndAdjust(pre, data, next)
        val after = this.freeSpace
        val diff = prefree - after
        debug { " data: ${data.toBytes().size} diff: $diff pre: $prefree after: $after " }
        pageSplitIfNecessary()
    }

    /**
     * find location where the record linked in page and return pre and next.
     * search only,don't affect the page .
     * use by [doInsertData] so if primary key duplication will throw [DuplicationPrimaryKeyException]
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
    fun targetSlotFirstUserRecord(targetSlot: Int): InnodbUserRecord {
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


    /**
     * data page will split when free space less than one in thirty-two page size
     */
    fun pageSplitIfNecessary() {
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
            PageSupport.flushPage(this)
            //  is root page
            val (leftPage, rightPage) = splitToSubPage(pageUserRecord, middleIndex, this)
            return this.pageType().rootUpgrade(leftPage, rightPage)
        }
        val (firstDataPage, secondDataPage) = splitToSubPage(pageUserRecord, middleIndex, this)
        //        Console.pageDescription(firstDataPage)
        //        Console.pageDescription(secondDataPage)
        upgrade(firstDataPage, secondDataPage)
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
        val leftPage = createFromUserRecords(recordList.subList(0, splitIndex), index)
        val rightPage = createFromUserRecords(recordList.subList(splitIndex, recordList.size), index)
        leftPage.fileHeader = FileHeader.create().apply {
            this.next = 0
            this.pre = 0
            this.offset = 0
            this.pageType = FIL_PAGE_INDEX_VALUE
        }

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
        val wrap = this.pageType().convertUserRecord(offsetInPage)
        wrap.setAbsoluteOffset(offsetInPage)
        return wrap
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


    /**
     * get the first index node to parent node insert
     * link [PageType.pageIndex]
     */
    fun pageIndex(): InnodbUserRecord {
        return pageType().pageIndex()
    }

    /**
     * page upgrade.
     * current page upgrade to parent
     * create 2 new page and linked whether upgrading  from index page or data page.
     * @param preChild left node
     * @param secondChild right node
     */
    private fun upgrade(preChild: InnoDbPage, secondChild: InnoDbPage) {
        preChild.pageHeader.level = pageHeader.level
        secondChild.pageHeader.level = pageHeader.level
        pageHeader.level++
        val firstFileHeader: FileHeader = preChild.fileHeader
        val secondFileHeader: FileHeader = secondChild.fileHeader
        val offset: Int = PageSupport.allocatePage(ext.belongIndex, 2)
        firstFileHeader.offset = offset
        secondFileHeader.offset = (offset + ConstantSize.PAGE.size())
        firstFileHeader.pre = (-1)
        firstFileHeader.next = (secondFileHeader.offset)
        secondFileHeader.pre = (firstFileHeader.offset)
        secondFileHeader.next = (-1)
        //  transfer to index page
        fileHeader.next = -1
        fileHeader.pageType = FIL_PAGE_INODE
        pageHeader = PageHeader.create()
        pageDirectory = PageDirectory()
        //  clear user record
        userRecords = UserRecords()
        infimum = Infimum.create(preChild)
        supremum = Supremum.create(secondChild)
        InnodbSessionContext.getInnodbSessionContext().modifyPage(preChild)
        InnodbSessionContext.getInnodbSessionContext().modifyPage(secondChild)
        insertData(preChild.pageIndex())
        insertData(secondChild.pageIndex())
    }

    //
    //    fun upgrade(prePage: InnoDbPage, nextPage: InnoDbPage) {
    //        //  is root page
    //        if (ext.parent == null) {
    //            rootPageUpgrade(prePage, nextPage)
    //        } else {
    //            // normal leaf node
    //            prePage.fileHeader.offset = fileHeader.offset
    //            val newDataPageOffset: Int = PageSupport.allocatePage(ext.belongIndex)
    //            nextPage.fileHeader.offset = newDataPageOffset
    //            val parent: InnoDbPage = ext.parent!!
    //            transferFrom(prePage)
    //            parent.insertData(nextPage.pageIndex())
    //        }
    //    }


    private fun linkedAndAdjust(pre: InnodbUserRecord, insertRecord: InnodbUserRecord, next: InnodbUserRecord) {
        insertRecord.apply {
            setAbsoluteOffset(pageHeader.heapTop + insertRecord.beforeSplitOffset())
            this.recordHeader.heapNo = pageHeader.absoluteRecordCount.toUInt()
            this.recordHeader.nextRecordOffset = next.absoluteOffset() - insertRecord.absoluteOffset()
        }
        pre.recordHeader.nextRecordOffset = insertRecord.absoluteOffset() - pre.absoluteOffset()
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
        debug { "start group split ..." }
        val nextGroupIndex = pageDirectory.slots.indexOfFirst { it.toInt() == groupMax.absoluteOffset() }
        var preMaxRecord = getUserRecordByOffset(pageDirectory.slots[nextGroupIndex + 1].toInt())
        val leftGroupCount = Constant.SLOT_MAX_COUNT shr 1
        val rightGroupCount = Constant.SLOT_MAX_COUNT - leftGroupCount + 1
        for (j in 0 until leftGroupCount) {
            preMaxRecord = getUserRecordByOffset(preMaxRecord.absoluteOffset() + preMaxRecord.nextRecordOffset())
        }
        preMaxRecord.recordHeader.nOwned = leftGroupCount
        this.refreshRecordHeader(preMaxRecord)
        groupMax.recordHeader.nOwned = rightGroupCount
        this.refreshRecordHeader(groupMax)
        this.pageHeader.slotCount = (this.pageHeader.slotCount.toInt() + 1).toShort()
        pageDirectory.split(nextGroupIndex, preMaxRecord.absoluteOffset().toShort())
        debug { "end group split ..." }
    }

    /**
     * byte array copy from target page
     */
    private fun transferFrom(page: InnoDbPage) {
        val snapshot: InnoDbPage = swap(page.toBytes(), ext.belongIndex)
        fileHeader = snapshot.fileHeader
        pageHeader = snapshot.pageHeader
        infimum = snapshot.infimum
        supremum = snapshot.supremum
        userRecords = snapshot.userRecords
        pageDirectory = snapshot.pageDirectory
        fileTrailer = snapshot.fileTrailer
        ext = snapshot.ext
    }

    /**
     * you should rewrote to page when you update user record that resolve by [getUserRecordByOffset]
     */
    private fun refreshRecordHeader(record: InnodbUserRecord) {
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

    class PageExt {

        lateinit var belongIndex: InnodbIndex

        var parent: InnoDbPage? = null

        var pageType: PageType? = null

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

        fun createFromUserRecords(recordList: List<InnodbUserRecord>, index: InnodbIndex): InnoDbPage {
            val dataPage = InnoDbPage(index)
            fillInnodbUserRecords(recordList, dataPage)
            dataPage.fileHeader.pageType = FIL_PAGE_INDEX_VALUE
            return dataPage
        }

        private fun fillInnodbUserRecords(recordList: List<InnodbUserRecord>, page: InnoDbPage) {
            page.fileHeader = FileHeader.create()
            page.supremum = Supremum.create(page)
            page.infimum = Infimum.create(page)
            val pageHeader = PageHeader.create().apply {
                this.slotCount = ((recordList.size + 1) / 8 + 1).toShort()
                this.absoluteRecordCount = (2 + recordList.size).toShort()
                this.recordCount = recordList.size.toShort()
            }
            page.pageHeader = pageHeader
            val slots = ShortArray((recordList.size + 1) / Constant.SLOT_MAX_COUNT + 1)
            slots[0] = ConstantSize.SUPREMUM.offset().toShort()
            slots[slots.size - 1] = ConstantSize.INFIMUM.offset().toShort()
            page.pageDirectory = PageDirectory(slots)
            page.userRecords = UserRecords().apply { addRecords(recordList) }
            var pre: InnodbUserRecord = page.infimum
            var preOffset: Short = ConstantSize.INFIMUM.offset().toShort()
            for (i in recordList.indices) {
                val current: InnodbUserRecord = recordList[i]
                val currentOffset: Int = pageHeader.lastInsertOffset + current.beforeSplitOffset()
                pageHeader.lastInsertOffset = (pageHeader.lastInsertOffset + current.length()).toShort()
                pre.recordHeader.nextRecordOffset = currentOffset - preOffset
                pre = current
                preOffset = currentOffset.toShort()
                if ((i + 1) % Constant.SLOT_MAX_COUNT == 0) {
                    slots[slots.size - 1 - (i + 1) % Constant.SLOT_MAX_COUNT] = currentOffset.toShort()
                }
            }
            pre.recordHeader.nextRecordOffset = ConstantSize.SUPREMUM.offset()
            page.fileTrailer = FileTrailer.create()
        }

        fun createRootPage(index: InnodbIndex) = InnoDbPage(index).apply {
            this.fileHeader = FileHeader.create()
            this.pageHeader = PageHeader.create()
            this.infimum = Infimum.create(this)
            this.supremum = Supremum.create(this)
            this.userRecords = UserRecords()
            this.pageDirectory = PageDirectory()
            this.fileTrailer = FileTrailer.create()
        }

        /**
         * swap byte array to page
         */
        fun swap(bytes: ByteArray, index: InnodbIndex): InnoDbPage {
            ConstantSize.PAGE.checkSize(bytes)
            val buffer: DynamicByteBuffer = DynamicByteBuffer.wrap(bytes)
            //  file header
            val fileHeaderBytes: ByteArray = buffer.getLength(ConstantSize.FILE_HEADER.size())
            val fileHeader = FileHeader.wrap(fileHeaderBytes)
            val page = InnoDbPage(index)
            return page.apply {
                page.fileHeader = fileHeader
                val pageHeaderBytes = buffer.getLength(ConstantSize.PAGE_HEADER.size())
                this.pageHeader = PageHeader.wrap(pageHeaderBytes)
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
                this.pageDirectory = PageDirectory(slots)
            }.apply {
                val bodyLength: Int = pageHeader.heapTop - PageHeader.EMPTY_PAGE_HEAP_TOP
                val body = Arrays.copyOfRange(
                    bytes, PageHeader.EMPTY_PAGE_HEAP_TOP.toInt(),
                    PageHeader.EMPTY_PAGE_HEAP_TOP + bodyLength
                )
                val userRecords = UserRecords(body)
                this.userRecords = userRecords
            }.apply {
                val trailerArr = Arrays.copyOfRange(bytes, bytes.size - ConstantSize.FILE_TRAILER.size(), bytes.size)
                this.fileTrailer = FileTrailer.wrap(trailerArr)
            }
        }

        fun findPageByOffset(pageOffset: Int, index: InnodbIndex): InnoDbPage {
            val file: File = index.file
            RandomAccessFile(file, "rw").use { randomAccessFile ->
                randomAccessFile.seek(pageOffset.toLong())
                val pageArr: ByteArray = ConstantSize.PAGE.emptyBuff()
                randomAccessFile.readFully(pageArr)
                return swap(pageArr, index)
            }
        }


    }
}
