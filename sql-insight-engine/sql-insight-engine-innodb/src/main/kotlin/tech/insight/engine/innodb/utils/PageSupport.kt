package tech.insight.engine.innodb.utils

import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import tech.insight.core.bean.Index
import tech.insight.core.exception.RuntimeIoException
import tech.insight.core.logging.Logging
import tech.insight.engine.innodb.page.ConstantSize
import tech.insight.engine.innodb.page.InnoDbPage

/**
 * @author gongxuanzhangmelt@gmail.com
 */
object PageSupport : Logging() {


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
