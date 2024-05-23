package tech.insight.engine.innodb.page

import com.fasterxml.jackson.module.kotlin.treeToValue
import org.gongxuanzhang.easybyte.core.ByteWrapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import tech.insight.core.bean.Table
import tech.insight.core.extension.mapper
import tech.insight.core.extension.slf4j
import tech.insight.core.extension.tree
import tech.insight.engine.innodb.index.ClusteredIndex
import tech.insight.engine.innodb.index.InnodbIndex
import tech.insight.engine.innodb.page.PageHeader.PageHeaderFactory.EMPTY_PAGE_HEAP_TOP
import kotlin.random.Random


class PageObjectTest {

    companion object {
        val log = slf4j<PageObjectTest>()
    }

    @Test
    fun testInnodbPageObjectLength() {
        val mockPage = mock<InnoDbPage>()
        assertEquals(ConstantSize.INFIMUM.size(), Infimum.create(mockPage).toBytes().size)
        assertEquals(ConstantSize.SUPREMUM.size(), Supremum.create(mockPage).toBytes().size)
        assertEquals(ConstantSize.PAGE_HEADER.size(), PageHeader.create().toBytes().size)
        assertEquals(ConstantSize.FILE_HEADER.size(), FileHeader.create().toBytes().size)
        assertEquals(ConstantSize.FILE_TRAILER.size(), FileTrailer.create().toBytes().size)
        assertEquals(ConstantSize.INFIMUM.size(), Infimum.create(mockPage).length())
        assertEquals(ConstantSize.SUPREMUM.size(), Supremum.create(mockPage).length())
        assertEquals(ConstantSize.PAGE_HEADER.size(), PageHeader.create().length())
        assertEquals(ConstantSize.FILE_HEADER.size(), FileHeader.create().length())
        assertEquals(ConstantSize.FILE_TRAILER.size(), FileTrailer.create().length())
    }

    @Test
    fun testPageHeaderWrap() {
        val pageHeader = fillRandomNumber(PageHeader.create())
        assertByteWrapper(pageHeader, PageHeader::wrap)
    }

    @Test
    fun testFileHeaderWrap() {
        val fileHeader = fillRandomNumber(FileHeader.create())
        assertByteWrapper(fileHeader, FileHeader::wrap)
    }

    @Test
    fun testInfimumWrap() {
        val mockPage = mock<InnoDbPage>()
        val infimum = Infimum.create(mockPage)
        assertByteWrapper(infimum) {
            Infimum.wrap(it, mockPage)
        }
    }

    @Test
    fun testSupremumWrap() {
        val mockPage = mock<InnoDbPage>()
        val supremum = Supremum.create(mockPage)
        assertByteWrapper(supremum) {
            Supremum.wrap(it, mockPage)
        }
    }

    @Test
    fun testFileTrailerWrap() {
        val fileTrailer = FileTrailer.create()
        assertByteWrapper(fileTrailer, FileTrailer::wrap)
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
        val pageHeader = fillRandomNumber(PageHeader.create())
        assertByteWrapper(pageHeader, PageHeader::wrap)
        val fileHeader = fillRandomNumber(FileHeader.create())
        assertByteWrapper(fileHeader, FileHeader::wrap)
        assertByteWrapper(Infimum.create(mockPage)) {
            Infimum.wrap(it, mockPage)
        }
        assertByteWrapper(Supremum.create(mockPage)) {
            Supremum.wrap(it, mockPage)
        }
        assertByteWrapper(FileTrailer.create(), FileTrailer::wrap)

        val table = /*TableManager.require(testDb, test_table)*/ mock<Table>()
        val index = ClusteredIndex(table)
        val dataPage = mock<InnoDbPage>()
        dataPage.fileHeader = FileHeader.create()
        dataPage.pageHeader = PageHeader.create().also { it.heapTop = (EMPTY_PAGE_HEAP_TOP + 16).toShort() }
        dataPage.supremum = Supremum.create(dataPage)
        dataPage.infimum = Infimum.create(dataPage)
        dataPage.userRecords =
            UserRecords(ByteArray(16) { it.times(2).toByte() })
        dataPage.pageDirectory =
            PageDirectory(ShortArray(3) { it.plus(1).toShort() })
        dataPage.fileTrailer = FileTrailer.create()

        assertByteWrapper(dataPage) { InnoDbPage.swap(it, index) }
    }


    private fun assertByteWrapper(source: ByteWrapper, wrapperAction: (ByteArray) -> ByteWrapper) {
        val invoke = wrapperAction.invoke(source.toBytes())
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
