package tech.insight.buffer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class ByteExtensionKtTest {


    @Test
    fun testSetOne() {
        val result = 0.toByte().setOne(0)
        assertEquals(0b00000001.toByte(), result)
    }

    @Test
    fun testSetZero() {
        val result = 1.toByte().setZero(0)
        assertEquals(0b00000000.toByte(), result)
    }

    @Test
    fun testIsOne() {
        val byte = 0b00000001.toByte()
        assertEquals(true, byte.isOne(0))
        assertEquals(false, byte.isOne(1))
    }

    @Test
    fun testIsZero() {
        val byte = 0b00000001.toByte()
        assertEquals(false, byte.isZero(0))
        assertEquals(true, byte.isZero(1))
    }
    
    @Test
    fun testSubByte() {
        val byte = 0b01011100.toByte()
        assertEquals(0b1100, byte.subByte(4))
        assertEquals(0b100, byte.subByte(3))
        assertEquals(0b011, byte.subByte(3,6))
        assertEquals(0b0111, byte.subByte(2,6))
        
    }

    @Test
    fun testErrorInvoke() {
        val byte = 0b00000001.toByte()
        assertThrows<IllegalArgumentException> { byte.isOne(8) }
        assertThrows<IllegalArgumentException> { byte.isZero(8) }
        assertThrows<IllegalArgumentException> { byte.setOne(8) }
        assertThrows<IllegalArgumentException> { byte.setZero(8) }
        assertThrows<IllegalArgumentException> { byte.subByte(8) }
    }

}
