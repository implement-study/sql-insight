package tech.insight.engine.innodb.page

import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import tech.insight.buffer.byteBuf


class PageDirectoryTest {

    @Test
    fun split() {
        val dir = mock<PageDirectory>()
        val byteBuf = run {
            byteBuf()
                .writeShort(50)
                .writeShort(40)
                .writeShort(30)
                .writeShort(20)
                .writeShort(10)
        }
        val shortArray = shortArrayOf(10, 20, 30, 40, 50)
        whenever(dir.source).doReturn(byteBuf)
        whenever(dir.slots).doReturn(shortArray)

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
