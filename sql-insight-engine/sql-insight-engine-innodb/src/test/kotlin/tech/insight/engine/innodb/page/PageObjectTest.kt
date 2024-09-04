package tech.insight.engine.innodb.page

import com.fasterxml.jackson.module.kotlin.treeToValue
import org.gongxuanzhang.easybyte.core.ByteWrapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import tech.insight.buffer.copyBuf
import tech.insight.core.bean.Table
import tech.insight.core.extension.mapper
import tech.insight.core.extension.tree
import tech.insight.engine.innodb.index.ClusteredIndex
import kotlin.random.Random


class PageObjectTest {


    @Test
    fun testFileHeaderWrap() {
        val mockPage = mock<InnoDbPage>()
        val fileHeader = fillRandomNumber(FileHeader.create(mockPage))
        assertByteWrapper(fileHeader, mockPage, FileHeader::wrap)
    }

    @Test
    fun testInfimumWrap() {
        val mockPage = mock<InnoDbPage>()
        val infimum = Infimum(mockPage)
        assertByteWrapper(infimum, mockPage, Infimum::wrap)
    }

    @Test
    fun testSupremumWrap() {
        val mockPage = mock<InnoDbPage>()
        val supremum = Supremum(mockPage)
        assertByteWrapper(supremum, mockPage, Supremum::wrap)
    }

    @Test
    fun testFileTrailerWrap() {
        val mockPage = mock<InnoDbPage>()
        val fileTrailer = FileTrailer.create(mockPage)
        assertByteWrapper(fileTrailer, mockPage, FileTrailer::wrap)
    }


    @Test
    fun testWrap() {
        val mockPage = mock<InnoDbPage>()
        //        assertByteWrapper(pageHeader, mockPage, PageHeader::)
        val fileHeader = fillRandomNumber(FileHeader.create(mockPage))
        assertByteWrapper(fileHeader, mockPage, FileHeader::wrap)
        assertByteWrapper(Infimum(mockPage), mockPage, Infimum::wrap)
        assertByteWrapper(Supremum(mockPage), mockPage, Supremum::wrap)
        assertByteWrapper(FileTrailer.create(mockPage), mockPage, FileTrailer::wrap)
        TODO()
        val table = /*TableManager.require(testDb, test_table)*/ mock<Table>()
        val index = ClusteredIndex(table)
        val dataPage = mock<InnoDbPage>()

        assertByteWrapper(dataPage, mockPage) { bytes, _ -> InnoDbPage(copyBuf(bytes), index) }
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
