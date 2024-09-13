package tech.insight.engine.innodb.page

import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import tech.insight.buffer.wrappedBuf
import kotlin.test.assertEquals


class PageDirectoryTest {


    val page = InnoDbPage(wrappedBuf(initPageArray()), mock()).apply {
        pageDirectory.insert(0, 1)
        pageDirectory.insert(0, 10)
    }
   
    
    @Test
    fun testInsert() {
        val page = InnoDbPage(wrappedBuf(initPageArray()), mock())
        assertEquals(
            page.pageDirectory.slots.toList(),
            listOf(Supremum.OFFSET_IN_PAGE, 10, 1, Infimum.OFFSET_IN_PAGE)
        )

    }

    @Test
    fun removeSlot() {
    }

    @Test
    fun replace() {
    }

    @Test
    fun get() {
    }

    @Test
    fun preTargetOffset() {
    }

    @Test
    fun nextTargetOffset() {
    }

    @Test
    fun indexSlot() {
    }
}
