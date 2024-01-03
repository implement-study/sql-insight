package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page

import org.gongxuanzhang.easybyte.core.ByteWrapper
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.factory.FileHeaderFactory
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.factory.PageFactory
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.factory.PageHeaderFactory
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.factory.PageHeaderFactory.EMPTY_PAGE_HEAP_TOP
import org.gongxuanzhang.sql.insight.fillNumber
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class PageObjectTest {

    @Test
    fun testInnodbPageObjectLength() {
        assertEquals(ConstantSize.INFIMUM.size(), Infimum().toBytes().size)
        assertEquals(ConstantSize.SUPREMUM.size(), Supremum().toBytes().size)
        assertEquals(ConstantSize.PAGE_HEADER.size(), PageHeaderFactory.createPageHeader().toBytes().size)
        assertEquals(ConstantSize.FILE_HEADER.size(), FileHeaderFactory.createFileHeader().toBytes().size)
        assertEquals(ConstantSize.FILE_TRAILER.size(), FileTrailer().toBytes().size)
        assertEquals(ConstantSize.INFIMUM.size(), Infimum().length())
        assertEquals(ConstantSize.SUPREMUM.size(), Supremum().length())
        assertEquals(ConstantSize.PAGE_HEADER.size(), PageHeaderFactory.createPageHeader().length())
        assertEquals(ConstantSize.FILE_HEADER.size(), FileHeaderFactory.createFileHeader().length())
        assertEquals(ConstantSize.FILE_TRAILER.size(), FileTrailer().length())
    }

    @Test
    fun testWrap() {
        val pageHeader = fillNumber(PageHeader())
        assertByteWrapper(pageHeader, PageHeaderFactory::readPageHeader)
        val fileHeader = fillNumber(FileHeader())
        assertByteWrapper(fileHeader, FileHeaderFactory::readFileHeader)
        assertByteWrapper(Infimum(), PageFactory::readInfimum)
        assertByteWrapper(Supremum(), PageFactory::readSupremum)
        assertByteWrapper(FileTrailer(), PageFactory::readFileTrailer)
        val dataPage = DataPage(null)
        dataPage.fileHeader = FileHeader()
        dataPage.pageHeader = PageHeader().also { it.heapTop = (EMPTY_PAGE_HEAP_TOP + 16).toShort() }
        dataPage.supremum = Supremum()
        dataPage.infimum = Infimum()
        dataPage.userRecords = UserRecords(ByteArray(16) { it.times(2).toByte() })
        dataPage.pageDirectory = PageDirectory(ShortArray(3) { it.plus(1).toShort() })
        dataPage.fileTrailer = FileTrailer()

        assertByteWrapper(dataPage) { PageFactory.swap(it, null) }
    }


    private fun assertByteWrapper(source: ByteWrapper, wrapperAction: (ByteArray) -> ByteWrapper) {
        val invoke = wrapperAction.invoke(source.toBytes())
        assertEquals(invoke, source)
    }

}
