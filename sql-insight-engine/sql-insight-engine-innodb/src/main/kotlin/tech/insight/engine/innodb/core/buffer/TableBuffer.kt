package tech.insight.engine.innodb.core.buffer

import java.io.File
import java.io.RandomAccessFile
import tech.insight.core.annotation.Temporary
import tech.insight.core.bean.Table
import tech.insight.engine.innodb.index.InnodbIndex
import tech.insight.engine.innodb.page.ConstantSize
import tech.insight.engine.innodb.page.InnoDbPage
import tech.insight.engine.innodb.page.initPageArray
import tech.insight.engine.innodb.utils.PageSupport.info


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class TableBuffer(val table: Table) : PageBuffer, PageAllocator {

    @Temporary("lru cache page buffer")
    private val pageBuffers = mutableMapOf<Int, InnoDbPage>()

    override fun getPageAndCache(pageOffset: Int, index: InnodbIndex): InnoDbPage {
        return pageBuffers.computeIfAbsent(pageOffset) {
            readPage(pageOffset, index)
        }
    }

    override fun currentSize(): Int {
        return pageBuffers.values.sumOf { it.length() }
    }

    override fun allocatePage(index: InnodbIndex): InnoDbPage {
        RandomAccessFile(index.file, "rw").use { randomAccessFile ->
            val currentLength: Long = randomAccessFile.length()
            val expendLength = currentLength + ConstantSize.PAGE.size.toLong()
            randomAccessFile.setLength(expendLength)
            info("expend file [${index.file.name}] to $expendLength (${expendLength shr 14} page)")
            val allocatePage = InnoDbPage(wrappedBuf(initPageArray), index)
            allocatePage.fileHeader.offset = currentLength.toInt()
            pageBuffers[currentLength.toInt()] = allocatePage
            return allocatePage
        }
    }

    private fun readPage(pageOffset: Int, index: InnodbIndex): InnoDbPage {
        val file: File = index.file
        RandomAccessFile(file, "rw").use { randomAccessFile ->
            randomAccessFile.seek(pageOffset.toLong())
            val pageArr: ByteArray = ConstantSize.PAGE.emptyBuff()
            randomAccessFile.readFully(pageArr)
            return InnoDbPage(wrappedBuf(pageArr), index)
        }
    }

}
