package tech.insight.engine.innodb.page

import com.fasterxml.jackson.module.kotlin.treeToValue
import org.gongxuanzhang.easybyte.core.ByteWrapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import tech.insight.core.bean.Table
import tech.insight.core.extension.mapper
import tech.insight.core.extension.tree
import tech.insight.engine.innodb.index.ClusteredIndex
import tech.insight.engine.innodb.index.InnodbIndex
import tech.insight.engine.innodb.page.PageHeader.PageHeaderFactory.EMPTY_PAGE_HEAP_TOP
import kotlin.random.Random


class PageObjectTest {


    @Test
    fun testInnodbPageObjectLength() {
        val mockPage = mock<InnoDbPage>()
        assertEquals(ConstantSize.INFIMUM.size(), Infimum.create(mockPage).toBytes().size)
        assertEquals(ConstantSize.SUPREMUM.size(), Supremum.create(mockPage).toBytes().size)
        assertEquals(ConstantSize.PAGE_HEADER.size(), PageHeader.create(mockPage).toBytes().size)
        assertEquals(ConstantSize.FILE_HEADER.size(), FileHeader.create(mockPage).toBytes().size)
        assertEquals(ConstantSize.FILE_TRAILER.size(), FileTrailer.create(mockPage).toBytes().size)
        assertEquals(ConstantSize.INFIMUM.size(), Infimum.create(mockPage).length())
        assertEquals(ConstantSize.SUPREMUM.size(), Supremum.create(mockPage).length())
        assertEquals(ConstantSize.PAGE_HEADER.size(), PageHeader.create(mockPage).length())
        assertEquals(ConstantSize.FILE_HEADER.size(), FileHeader.create(mockPage).length())
        assertEquals(ConstantSize.FILE_TRAILER.size(), FileTrailer.create(mockPage).length())
    }

    @Test
    fun testPageHeaderWrap() {
        val mockPage = mock<InnoDbPage>()
        val pageHeader = fillRandomNumber(PageHeader.create(mockPage))
        assertByteWrapper(pageHeader, mockPage, PageHeader::wrap)
    }

    @Test
    fun testFileHeaderWrap() {
        val mockPage = mock<InnoDbPage>()
        val fileHeader = fillRandomNumber(FileHeader.create(mockPage))
        assertByteWrapper(fileHeader, mockPage, FileHeader::wrap)
    }

    @Test
    fun testInfimumWrap() {
        val mockPage = mock<InnoDbPage>()
        val infimum = Infimum.create(mockPage)
        assertByteWrapper(infimum, mockPage, Infimum::wrap)
    }

    @Test
    fun testSupremumWrap() {
        val mockPage = mock<InnoDbPage>()
        val supremum = Supremum.create(mockPage)
        assertByteWrapper(supremum, mockPage, Supremum::wrap)
    }

    @Test
    fun testFileTrailerWrap() {
        val mockPage = mock<InnoDbPage>()
        val fileTrailer = FileTrailer.create(mockPage)
        assertByteWrapper(fileTrailer, mockPage, FileTrailer::wrap)
    }

    @Test
    fun testRootPage() {
        val index = mock<InnodbIndex>()
        val page = InnoDbPage.createRootPage(index)
        val swap = InnoDbPage.swap(page.toBytes(), index)
        assertEquals(page, swap)
    }


    @Test
    fun testWrap() {
        val mockPage = mock<InnoDbPage>()
        val pageHeader = fillRandomNumber(PageHeader.create(mockPage))
        assertByteWrapper(pageHeader, mockPage, PageHeader::wrap)
        val fileHeader = fillRandomNumber(FileHeader.create(mockPage))
        assertByteWrapper(fileHeader, mockPage, FileHeader::wrap)
        assertByteWrapper(Infimum.create(mockPage), mockPage, Infimum::wrap)
        assertByteWrapper(Supremum.create(mockPage), mockPage, Supremum::wrap)
        assertByteWrapper(FileTrailer.create(mockPage), mockPage, FileTrailer::wrap)

        val table = /*TableManager.require(testDb, test_table)*/ mock<Table>()
        val index = ClusteredIndex(table)
        val dataPage = mock<InnoDbPage>()
        dataPage.fileHeader = FileHeader.create(mockPage)
        dataPage.pageHeader = PageHeader.create(mockPage).also { it.heapTop = (EMPTY_PAGE_HEAP_TOP + 16).toShort() }
        dataPage.supremum = Supremum.create(dataPage)
        dataPage.infimum = Infimum.create(dataPage)
        dataPage.userRecords = UserRecords.wrap(ByteArray(16) { it.times(2).toByte() }, mockPage)
        dataPage.pageDirectory = PageDirectory.wrap(ShortArray(3) { it.plus(1).toShort() }, mockPage)
        dataPage.fileTrailer = FileTrailer.create(mockPage)

        assertByteWrapper(dataPage, mockPage) { bytes, _ -> InnoDbPage.swap(bytes, index) }
    }


    private fun assertByteWrapper(
        source: ByteWrapper,
        innoDbPage: InnoDbPage,
        wrapperAction: (ByteArray, InnoDbPage) -> ByteWrapper
    ) {
        val invoke = wrapperAction.invoke(source.toBytes(), innoDbPage)
        assertEquals(invoke, source)
    }

    private inline fun <reified T> fillRandomNumber(t: T): T {
        val tree = t?.tree()!!
        tree.fields().forEach { (key, value) ->
            if (value.isNumber) {
                tree.put(key, Random.nextInt().toShort())
            }
        }
        return mapper().treeToValue(tree)
    }

}
