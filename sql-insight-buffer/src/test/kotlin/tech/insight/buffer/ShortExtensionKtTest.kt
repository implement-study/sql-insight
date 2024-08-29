package tech.insight.buffer

import java.nio.ByteBuffer
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class ShortExtensionKtTest {


    @Test
    fun testSetOneAndZero() {
        assertEquals(0b00000001.toShort(), 0.toShort().setOne(0))
        assertEquals(0b00000000.toShort(), 1.toShort().setZero(0))
    }

    @Test
    fun testIsOneAndZero() {
        val short = 0b00000001.toShort()
        assertEquals(true, short.isOne(0))
        assertEquals(false, short.isOne(1))
        assertEquals(false, short.isZero(0))
        assertEquals(true, short.isZero(1))
    }


    @Test
    fun testSubShort() {
        val short = 0b01011100.toShort()
        assertEquals(0b1100, short.subShort(4))
        assertEquals(0b100, short.subShort(3))
        assertEquals(0b011, short.subShort(3, 6))
        assertEquals(0b0111, short.subShort(2, 6))
    }

    @Test
    fun testByteArray() {
        for (i in Short.MIN_VALUE..Short.MAX_VALUE) {
            val buffer = ByteBuffer.allocate(Short.SIZE_BYTES)
            buffer.putShort(i.toShort())
            assertArrayEquals(buffer.array(), i.toShort().byteArray())
        }
    }

    @Test
    fun testCoverBits() {
        val short = 0b1101_0101.toShort()
        assertEquals(0b1101_0000.toShort(), short.coverBits(0, 4))
        assertEquals(0b1100_0000.toShort(), short.coverBits(0, 5))
        assertEquals(0x80.toShort(), 0xff.toShort().coverBits(0, 7))
        assertEquals(0x8000.toShort(), 0xffff.toShort().coverBits(0, 15))
        assertEquals(0b1111_1111_1111_0000_0011.toShort(), 0xffff.toShort().coverBits(0, 2, 8))
        assertEquals(0b0011.toShort(), 0xffff.toShort().coverBits(0, 2, 16))
    }
}
