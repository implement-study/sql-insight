package tech.insight.buffer

import io.netty.buffer.Unpooled
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class NettyByteBufTest {

    @Test
    fun testReadAllBytes() {
        val originalBytes = byteArrayOf(10, 20, 30, 40, 50)

        val byteBuf = Unpooled.wrappedBuffer(originalBytes)
        val readBytes = byteBuf.readAllBytes()

        assertEquals(originalBytes.toList(), readBytes.toList())
    }

    @Test
    fun testReadAllBytes_EmptyBuffer() {
        val byteBuf = Unpooled.buffer(0)
        val readBytes = byteBuf.readAllBytes()

        assertEquals(0, readBytes.size)
    }

    @Test
    fun testReadAllBytes_PartialRead() {
        val originalBytes = byteArrayOf(1, 2, 3, 4, 5)
        val byteBuf = Unpooled.wrappedBuffer(originalBytes)

        val readBytesPartial = byteBuf.readBytes(2)
        val readBytesAll = byteBuf.readAllBytes()

        assertEquals(listOf(3.toByte(), 4.toByte(), 5.toByte()), readBytesAll.toList())
        assertEquals(2, readBytesPartial.readableBytes())
    }
}
