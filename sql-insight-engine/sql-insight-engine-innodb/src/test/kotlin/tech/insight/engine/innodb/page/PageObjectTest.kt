package tech.insight.engine.innodb.page

import com.fasterxml.jackson.module.kotlin.treeToValue
import org.gongxuanzhang.easybyte.core.ByteWrapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tech.insight.core.environment.TableManager
import tech.insight.core.extension.mapper
import tech.insight.core.extension.tree
import tech.insight.engine.innodb.index.ClusteredIndex
import tech.insight.engine.innodb.page.PageHeader.PageHeaderFactory.EMPTY_PAGE_HEAP_TOP
import tech.insight.share.test.prepareTable
import tech.insight.share.test.testDb
import tech.insight.share.test.test_table
import kotlin.random.Random


class PageObjectTest {

    @Test
    fun testInnodbPageObjectLength() {
        assertEquals(ConstantSize.INFIMUM.size(), Infimum.create().toBytes().size)
        assertEquals(ConstantSize.SUPREMUM.size(), Supremum.create().toBytes().size)
        assertEquals(ConstantSize.PAGE_HEADER.size(), PageHeader.create().toBytes().size)
        assertEquals(ConstantSize.FILE_HEADER.size(), FileHeader.create().toBytes().size)
        assertEquals(ConstantSize.FILE_TRAILER.size(), FileTrailer.create().toBytes().size)
        assertEquals(ConstantSize.INFIMUM.size(), Infimum.create().length())
        assertEquals(ConstantSize.SUPREMUM.size(), Supremum.create().length())
        assertEquals(ConstantSize.PAGE_HEADER.size(), PageHeader.create().length())
        assertEquals(ConstantSize.FILE_HEADER.size(), FileHeader.create().length())
        assertEquals(ConstantSize.FILE_TRAILER.size(), FileTrailer.create().length())
    }

    @Test
    fun testWrap() {
        val pageHeader = fillRandomNumber(PageHeader.create())
        assertByteWrapper(pageHeader, PageHeader::wrap)
        val fileHeader = fillRandomNumber(FileHeader.create())
        assertByteWrapper(fileHeader, FileHeader::wrap)
        assertByteWrapper(Infimum.create(), Infimum::wrap)
        assertByteWrapper(Supremum.create(), Supremum::wrap)
        assertByteWrapper(FileTrailer.create(), FileTrailer::wrap)

        prepareTable()
        val table = TableManager.require(testDb, test_table)
        val index = ClusteredIndex(table)
        val dataPage = DataPage(index)
        dataPage.fileHeader = FileHeader.create()
        dataPage.pageHeader = PageHeader.create().also { it.heapTop = (EMPTY_PAGE_HEAP_TOP + 16).toShort() }
        dataPage.supremum = Supremum.create()
        dataPage.infimum = Infimum.create()
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
