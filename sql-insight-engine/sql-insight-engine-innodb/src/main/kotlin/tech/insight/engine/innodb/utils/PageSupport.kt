package tech.insight.engine.innodb.utils

import tech.insight.core.bean.Index
import tech.insight.core.exception.RuntimeIoException
import tech.insight.core.extension.slf4j
import tech.insight.engine.innodb.index.InnodbIndex
import tech.insight.engine.innodb.page.ConstantSize
import tech.insight.engine.innodb.page.InnoDbPage
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.RandomAccessFile

/**
 * @author gongxuanzhangmelt@gmail.com
 */
object PageSupport {
    private val log = slf4j<PageSupport>()

    fun getRoot(index: InnodbIndex): InnoDbPage {
        val indexFile: File = index.file
        FileInputStream(indexFile).use { fileInputStream ->
            val pageByte: ByteArray = ConstantSize.PAGE.emptyBuff()
            require(fileInputStream.read(pageByte) == pageByte.size) { "idb file error [ " + indexFile.getAbsoluteFile() + " ]" }
            return InnoDbPage.swap(pageByte, index)
        }
    }

    /**
     * extension index file page count * page size.
     *
     * @return offset namely length of file before extension.
     */
    @JvmOverloads
    fun allocatePage(index: InnodbIndex, pageCount: Int = 1): Int {
        RandomAccessFile(index.file, "rw").use { randomAccessFile ->
            val currentLength: Long = randomAccessFile.length()
            randomAccessFile.setLength(currentLength + ConstantSize.PAGE_HEADER.size().toLong() * pageCount)
            return currentLength.toInt()
        }
    }

    fun flushPage(page: InnoDbPage) {
        val belongIndex: Index = page.ext.belongIndex
        val indexFile: File = belongIndex.file
        try {
            RandomAccessFile(indexFile, "rw").use { randomAccessFile ->
                val minLength: Long = (page.fileHeader.offset + ConstantSize.PAGE.size()).toLong()
                if (randomAccessFile.length() < minLength) {
                    randomAccessFile.setLength(minLength)
                }
                randomAccessFile.seek(page.fileHeader.offset.toLong())
                randomAccessFile.write(page.toBytes())
                log.info("write page to {}", indexFile.canonicalPath)
            }
        } catch (e: IOException) {
            throw RuntimeIoException(e)
        }
    }

    fun flushPages(vararg pages: InnoDbPage) {
        for (page in pages) {
            flushPage(page)
        }
    }
}
