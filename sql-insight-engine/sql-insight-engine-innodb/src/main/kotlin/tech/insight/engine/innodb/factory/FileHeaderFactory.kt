package tech.insight.engine.innodb.factory

import tech.insight.engine.innodb.page.ConstantSize
import tech.insight.engine.innodb.page.FileHeader
import tech.insight.engine.innodb.page.PageType
import java.nio.ByteBuffer

/**
 * @author gongxuanzhangmelt@gmail.com
 */
object FileHeaderFactory {
    fun readFileHeader(arr: ByteArray?): FileHeader {
        ConstantSize.FILE_HEADER.checkSize(arr!!)
        val fileHeader = FileHeader()
        val buffer = ByteBuffer.wrap(arr)
        fileHeader.checkSum = buffer.getInt()
        fileHeader.offset = buffer.getInt()
        fileHeader.pageType = buffer.getShort()
        fileHeader.pre = buffer.getInt()
        fileHeader.next = buffer.getInt()
        fileHeader.lsn = buffer.getLong()
        fileHeader.flushLsn = buffer.getLong()
        fileHeader.spaceId = buffer.getInt()
        return fileHeader
    }

    /**
     * create a empty file header
     */
    fun createFileHeader(): FileHeader {
        val fileHeader = FileHeader()
        fileHeader.next = 0
        fileHeader.pre = 0
        fileHeader.offset = 0
        fileHeader.pageType = PageType.FIL_PAGE_INDEX.value
        return fileHeader
    }
}
