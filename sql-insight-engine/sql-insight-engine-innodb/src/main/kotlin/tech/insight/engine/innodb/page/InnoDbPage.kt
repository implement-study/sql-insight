package tech.insight.engine.innodb.page

import org.gongxuanzhang.easybyte.core.ByteWrapper
import org.gongxuanzhang.easybyte.core.DynamicByteBuffer
import tech.insight.core.exception.DuplicationPrimaryKeyException
import tech.insight.core.logging.Logging
import tech.insight.engine.innodb.index.InnodbIndex
import tech.insight.engine.innodb.page.compact.IndexRecord
import tech.insight.engine.innodb.page.compact.RecordHeader
import tech.insight.engine.innodb.page.compact.RecordType
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
abstract class InnoDbPage protected constructor(index: InnodbIndex) : Logging(), ByteWrapper,
    Comparator<InnodbUserRecord>,
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
        return PageType.Companion.valueOf(fileHeader.pageType.toInt())
    }

    val freeSpace: Short
        get() = (ConstantSize.PAGE.size() -
                ConstantSize.PAGE_HEADER.size() -
                ConstantSize.FILE_HEADER.size() -
                ConstantSize.FILE_TRAILER.size() -
                ConstantSize.SUPREMUM.size() -
                ConstantSize.INFIMUM.size() -
                pageDirectory.length() - userRecords.length()).toShort()

    /**
     * add a data.
     * data means a insert row .
     * page is leaf node will insert data.
     * page is index node will find target leaf node and insert data.
     * may be split page in process
     */
    open fun insertData(data: InnodbUserRecord) {
        val (pre, next) = findPreAndNext(data)
        val prefree = this.freeSpace
        linkedAndAdjust(pre, data, next)
        val after = this.freeSpace
        val diff = prefree - after
        println(" data: ${data.toBytes().size} diff: $diff pre: $prefree after: $after ")
        pageSplitIfNecessary()
        PageSupport.flushPage(this)
    }

    /**
     * find location where the record linked in page and return pre and next.
     * search only,don't affect the page .
     *
     * @param userRecord target record
     */
    protected fun findPreAndNext(userRecord: InnodbUserRecord): Pair<InnodbUserRecord, InnodbUserRecord> {
        val targetSlot = findTargetSlot(userRecord)
        var pre = getUserRecordByOffset(pageDirectory.indexSlot(targetSlot + 1).toInt())
        var next = getUserRecordByOffset(pre.nextRecordOffset() + pre.offset())
        while (compare(userRecord, next) > 0) {
            pre = next
            next = getUserRecordByOffset(pre.nextRecordOffset() + pre.offset())
        }
        return Pair(pre, next)
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
        var left = 0
        var right = pageDirectory.slotCount() - 1
        while (left < right - 1) {
            val mid = (right + left) / 2
            val offset: Short = pageDirectory.slots[mid]
            val base = getUserRecordByOffset(offset.toInt())
            val compare = compare(userRecord, base)
            if (compare == 0) {
                throw DuplicationPrimaryKeyException(base.rowId)
            }
            if (compare < 0) {
                left = mid
            } else {
                right = mid
            }
        }
        val base = getUserRecordByOffset(pageDirectory.slots[right].toInt())
        val compare = compare(userRecord, base)
        if (compare == 0) {
            throw DuplicationPrimaryKeyException(base.rowId)
        }
        return if (compare > 0) {
            left
        } else right
    }

    /**
     * @param offsetInPage offset in page
     * @return user record
     */
    protected fun getUserRecordByOffset(offsetInPage: Int): InnodbUserRecord {
        if (offsetInPage == ConstantSize.INFIMUM.offset()) {
            return infimum
        }
        if (offsetInPage == ConstantSize.SUPREMUM.offset()) {
            return supremum
        }
        val wrap = wrapUserRecord(offsetInPage)
        wrap.setOffset(offsetInPage)
        return wrap
    }


    protected abstract fun wrapUserRecord(offsetInPage: Int): InnodbUserRecord

    /**
     * this page should split.
     * in general after insert row call this method
     */
    protected abstract fun pageSplitIfNecessary()

    protected fun upgrade(prePage: InnoDbPage, nextPage: InnoDbPage) {
        //  is root page
        if (ext.parent == null) {
            rootPageUpgrade(prePage, nextPage)
        } else {
            // normal leaf node
            prePage.fileHeader.offset = fileHeader.offset
            val newDataPageOffset: Int = PageSupport.allocatePage(ext.belongIndex)
            nextPage.fileHeader.offset = newDataPageOffset
            val parent: InnoDbPage = ext.parent!!
            transferFrom(prePage)
            parent.insertData(nextPage.pageIndex())
        }
    }

    /**
     * get the first index node to parent node insert
     */
    abstract fun pageIndex(): IndexRecord

    /**
     * root page upgrade.
     * create 2 new page and linked whether upgrading  from index page or data page.
     */
    private fun rootPageUpgrade(preChild: InnoDbPage, secondChild: InnoDbPage) {
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
        fileHeader.pageType = PageType.FIL_PAGE_INODE.value
        pageHeader = PageHeader.create()
        pageDirectory = PageDirectory()
        //  clear user record
        userRecords = UserRecords()
        insertData(preChild.pageIndex())
        insertData(secondChild.pageIndex())
    }

    protected fun linkedAndAdjust(pre: InnodbUserRecord, insertRecord: InnodbUserRecord, next: InnodbUserRecord) {
        val insertHeader: RecordHeader = insertRecord.recordHeader
        insertHeader.setHeapNo(pageHeader.absoluteRecordCount.toUInt())
        insertRecord.setOffset(pageHeader.lastInsertOffset + insertRecord.beforeSplitOffset())
        insertHeader.setNextRecordOffset(next.offset() - insertRecord.offset())
        pre.recordHeader.setNextRecordOffset(insertRecord.offset() - pre.offset())
        refreshRecordHeader(pre)
        insertHeader.setRecordType(RecordType.NORMAL)

        //  adjust page
        userRecords.addRecord(insertRecord)
        pageHeader.absoluteRecordCount++
        pageHeader.recordCount++
        pageHeader.heapTop = (pageHeader.heapTop + insertRecord.length().toShort()).toShort()
        pageHeader.lastInsertOffset = (pageHeader.lastInsertOffset + insertRecord.length().toShort()).toShort()

        var groupMax = next
        //  adjust group
        while (groupMax.recordHeader.nOwned == 0) {
            groupMax = getUserRecordByOffset(next.offset() + next.nextRecordOffset())
        }
        val groupMaxHeader: RecordHeader = groupMax.recordHeader
        val groupCount: Int = groupMaxHeader.nOwned
        groupMaxHeader.setNOwned(groupCount + 1)
        if (next.recordHeader.nOwned <= Constant.SLOT_MAX_COUNT) {
            return refreshRecordHeader(next)
        }
        debug { "start group split ..." }
        val nextGroupIndex = pageDirectory.slots.indexOfFirst { it.toInt() == groupMax.offset() }
        var preMaxRecord = getUserRecordByOffset(pageDirectory.slots[nextGroupIndex + 1].toInt())
        val leftGroupCount = Constant.SLOT_MAX_COUNT shr 1
        val rightGroupCount = Constant.SLOT_MAX_COUNT - leftGroupCount + 1
        for (j in 0 until leftGroupCount) {
            preMaxRecord = getUserRecordByOffset(preMaxRecord.offset() + preMaxRecord.nextRecordOffset())
        }
        preMaxRecord.apply {
            recordHeader.setNOwned(leftGroupCount)
            refreshRecordHeader(this)
        }
        groupMax.apply {
            recordHeader.setNOwned(rightGroupCount)
            refreshRecordHeader(this)
        }
        pageDirectory.split(nextGroupIndex, preMaxRecord.offset().toShort())
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

    private fun refreshRecordHeader(record: InnodbUserRecord) {
        if (record is Infimum) {
            return
        }
        if (record is Supremum) {
            return
        }
        val headerByte = record.recordHeader.toBytes()
        val bodyOffset: Int = record.offset() - ConstantSize.RECORD_HEADER.size() - ConstantSize.PAGE_HEADER.size() -
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
        private var cursor = getUserRecordByOffset(infimum.nextRecordOffset() + infimum.offset())
        override fun hasNext(): Boolean {
            return cursor !== supremum
        }

        override fun next(): InnodbUserRecord {
            if (cursor === supremum) {
                throw NoSuchElementException()
            }
            val result = cursor
            cursor = getUserRecordByOffset(cursor.nextRecordOffset() + cursor.offset())
            return result
        }

    }

    class PageExt {

        lateinit var belongIndex: InnodbIndex

        var parent: IndexPage? = null

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

        fun createRootPage(index: InnodbIndex) = DataPage(index).apply {
            this.fileHeader = FileHeader.create()
            this.pageHeader = PageHeader.create()
            this.infimum = Infimum.create()
            this.supremum = Supremum.create()
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
            val page = if (fileHeader.pageType == PageType.FIL_PAGE_INODE.value) {
                IndexPage(index)
            } else {
                DataPage(index)
            }
            return page.apply {
                page.fileHeader = fileHeader
                val pageHeaderBytes = buffer.getLength(ConstantSize.PAGE_HEADER.size())
                this.pageHeader = PageHeader.wrap(pageHeaderBytes)
            }.apply {
                val infimumBytes = buffer.getLength(ConstantSize.INFIMUM.size())
                this.infimum = Infimum.wrap(infimumBytes)
                val supremumBytes: ByteArray = buffer.getLength(ConstantSize.SUPREMUM.size())
                this.supremum = Supremum.wrap(supremumBytes)
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


        /**
         * page split create a new data page.
         *
         * @param recordList data in the page that sorted
         * @return the data page but the file header , page header is not complete
         */
        fun createDataPage(recordList: List<InnodbUserRecord>, index: InnodbIndex): DataPage {
            val dataPage = DataPage(index)
            fillInnodbUserRecords(recordList, dataPage)
            dataPage.fileHeader.pageType = PageType.FIL_PAGE_INDEX.value
            return dataPage
        }

        fun createIndexPage(indexRecordList: List<InnodbUserRecord>, index: InnodbIndex): IndexPage {
            val indexPage = IndexPage(index)
            fillInnodbUserRecords(indexRecordList, indexPage)
            indexPage.fileHeader.pageType = PageType.FIL_PAGE_INODE.value
            return indexPage
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
