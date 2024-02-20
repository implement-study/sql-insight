package tech.insight.engine.innodb.factory

import org.gongxuanzhang.easybyte.core.DynamicByteBuffer
import tech.insight.core.bean.Table
import tech.insight.core.exception.RuntimeIoException
import tech.insight.core.extension.slf4j
import tech.insight.engine.innodb.index.ClusteredIndex
import tech.insight.engine.innodb.index.InnodbIndex
import tech.insight.engine.innodb.page.*
import tech.insight.engine.innodb.page.compact.RecordHeader
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.file.Files
import java.util.*

/**
 * @author gongxuanzhangmelt@gmail.com
 */
object PageFactory {
    val log = slf4j<PageFactory>()

    /**
     * create idb file and add a root page .
     */
    fun initialization(table: Table) {
        val clusteredIndex = ClusteredIndex(table)
        val primaryFile: File = clusteredIndex.file
        if (!primaryFile.createNewFile()) {
            log.warn("{} already exists , execute create table will overwrite file", primaryFile.getAbsoluteFile())
        }
        val root = createRoot(clusteredIndex)
        Files.write(primaryFile.toPath(), root.toBytes())
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
        val fileHeader = FileHeader()
        page.fileHeader = fileHeader
        val pageHeader = PageHeader()
        pageHeader.slotCount = ((recordList.size + 1) / 8 + 1).toShort()
        pageHeader.absoluteRecordCount = (2 + recordList.size).toShort()
        pageHeader.recordCount = recordList.size.toShort()
        pageHeader.lastInsertOffset = ConstantSize.USER_RECORDS.offset().toShort()
        page.supremum = Supremum()
        page.infimum = Infimum()
        val slots = ShortArray((recordList.size + 1) / Constant.SLOT_MAX_COUNT + 1)
        page.pageDirectory = PageDirectory(slots)
        page.fileTrailer = FileTrailer()
        val userRecords = UserRecords()
        page.userRecords = userRecords
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
        page.pageDirectory = PageDirectory(slots)
        slots[0] = ConstantSize.SUPREMUM.offset().toShort()
        slots[slots.size - 1] = ConstantSize.INFIMUM.offset().toShort()
        page.fileTrailer = FileTrailer()
    }

    fun findPageByOffset(pageOffset: Int, index: InnodbIndex): InnoDbPage {
        val file: File = index.file
        try {
            RandomAccessFile(file, "rw").use { randomAccessFile ->
                randomAccessFile.seek(pageOffset.toLong())
                val pageArr: ByteArray = ConstantSize.PAGE.emptyBuff()
                randomAccessFile.readFully(pageArr)
                return swap(pageArr, index)
            }
        } catch (e: IOException) {
            throw RuntimeIoException(e)
        }
    }

    /**
     * swap byte array to page
     */
    fun swap(bytes: ByteArray, index: InnodbIndex): InnoDbPage {
        ConstantSize.PAGE.checkSize(bytes)
        val buffer: DynamicByteBuffer = DynamicByteBuffer.wrap(bytes)
        //  file header
        val fileHeaderBytes: ByteArray = buffer.getLength(ConstantSize.FILE_HEADER.size())
        val fileHeader = FileHeaderFactory.readFileHeader(fileHeaderBytes)
        // page header
        val pageHeaderBytes: ByteArray = buffer.getLength(ConstantSize.PAGE_HEADER.size())
        val pageHeader: PageHeader = PageHeaderFactory.readPageHeader(pageHeaderBytes)
        //  infimum
        val infimumBytes: ByteArray = buffer.getLength(ConstantSize.INFIMUM.size())
        val infimum: Infimum = readInfimum(infimumBytes)
        //  supremum
        val supremumBytes: ByteArray = buffer.getLength(ConstantSize.SUPREMUM.size())
        val supremum: Supremum = readSupremum(supremumBytes)
        val result: InnoDbPage = if (fileHeader.pageType == PageType.FIL_PAGE_INODE.value) {
            IndexPage(index)
        } else {
            DataPage(index)
        }
        result.fileHeader = fileHeader
        result.pageHeader = pageHeader
        result.infimum = infimum
        result.supremum = supremum
        //   file trailer
        val trailerArr = Arrays.copyOfRange(bytes, bytes.size - ConstantSize.FILE_TRAILER.size(), bytes.size)
        result.fileTrailer = readFileTrailer(trailerArr)
        //   page directory
        var dirOffset: Int = bytes.size - ConstantSize.FILE_TRAILER.size() - java.lang.Short.BYTES
        val byteBuffer = ByteBuffer.wrap(bytes)
        val shortList: MutableList<Short> = ArrayList()
        var slot = byteBuffer.getShort(dirOffset)
        while (slot.toInt() != 0) {
            shortList.add(slot)
            dirOffset -= java.lang.Short.BYTES
            slot = byteBuffer.getShort(dirOffset)
        }
        val slots = ShortArray(shortList.size)
        for (i in slots.indices) {
            slots[i] = shortList[shortList.size - 1 - i]
        }
        result.pageDirectory = PageDirectory(slots)
        //  user records
        val bodyLength: Int = pageHeader.heapTop - PageHeaderFactory.EMPTY_PAGE_HEAP_TOP
        val body = Arrays.copyOfRange(
            bytes, PageHeaderFactory.EMPTY_PAGE_HEAP_TOP.toInt(),
            PageHeaderFactory.EMPTY_PAGE_HEAP_TOP + bodyLength
        )
        val userRecords = UserRecords(body)
        result.userRecords = userRecords
        return result
    }

    private fun createRoot(index: InnodbIndex): InnoDbPage {
        val root = DataPage(index)
        root.fileHeader = FileHeaderFactory.createFileHeader()
        root.pageHeader = PageHeaderFactory.createPageHeader()
        root.infimum = Infimum()
        root.supremum = Supremum()
        root.userRecords = UserRecords()
        root.pageDirectory = PageDirectory()
        root.fileTrailer = FileTrailer()
        return root
    }

    fun readSupremum(bytes: ByteArray): Supremum {
        ConstantSize.SUPREMUM.checkSize(bytes)
        val buffer: DynamicByteBuffer = DynamicByteBuffer.wrap(bytes)
        val supremum = Supremum()
        val headBuffer: ByteArray = buffer.getLength(ConstantSize.RECORD_HEADER.size())
        supremum.setRecordHeader(RecordHeader(headBuffer))
        return supremum
    }

    fun readInfimum(bytes: ByteArray): Infimum {
        ConstantSize.INFIMUM.checkSize(bytes)
        val buffer: DynamicByteBuffer = DynamicByteBuffer.wrap(bytes)
        val headBuffer: ByteArray = buffer.getLength(ConstantSize.RECORD_HEADER.size())
        val infimum = Infimum()
        infimum.setRecordHeader(RecordHeader(headBuffer))
        return infimum
    }

    fun readFileTrailer(bytes: ByteArray): FileTrailer {
        ConstantSize.FILE_TRAILER.checkSize(bytes)
        val buffer: DynamicByteBuffer = DynamicByteBuffer.wrap(bytes)
        val fileTrailer = FileTrailer()
        fileTrailer.checkSum = buffer.int
        fileTrailer.lsn = buffer.int
        return fileTrailer
    }
}
