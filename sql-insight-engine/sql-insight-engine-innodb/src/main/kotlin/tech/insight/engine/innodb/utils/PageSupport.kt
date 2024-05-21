package tech.insight.engine.innodb.utils

import tech.insight.core.bean.Index
import tech.insight.core.exception.RuntimeIoException
import tech.insight.core.logging.Logging
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
object PageSupport : Logging() {

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
    fun allocatePage(index: InnodbIndex, pageCount: Int = 1): Int {
        RandomAccessFile(index.file, "rw").use { randomAccessFile ->
            val currentLength: Long = randomAccessFile.length()
            val expendLength = currentLength + ConstantSize.PAGE.size().toLong() * pageCount
            randomAccessFile.setLength(expendLength)
            info("expend file [${index.file.name}] to $expendLength")
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
                debug { "write page to ${indexFile.canonicalPath}" }
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
