package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page

import org.gongxuanzhang.easybyte.core.ByteWrapper
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.factory.FileHeaderFactory
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.factory.PageFactory
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.factory.PageHeaderFactory
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
        assertByteWrapper(fileHeader,FileHeaderFactory::readFileHeader)
        assertByteWrapper(Infimum(),PageFactory::readInfimum)
        assertByteWrapper(Supremum(),PageFactory::readSupremum)
    }


    private fun assertByteWrapper(source: ByteWrapper, wrapperAction: (ByteArray) -> ByteWrapper) {
        assertEquals(wrapperAction.invoke(source.toBytes()), source)
    }

}
