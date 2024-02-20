package tech.insight.core.extension

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.nio.ByteBuffer


class NumberExtensionKtTest {


    @Test
    fun toBytesTest() {
        val i = 123123
        val expect = ByteBuffer.allocate(Int.SIZE_BYTES).putInt(i).array()
        assertEquals(expect, i.toByteArray())
    }

    @Test
    fun set1Test() {
        val a: Byte = 1
        assertEquals(0, a.setBit0(0))
        assertEquals(3, a.setBit1(1))
        assertThrows<IllegalStateException> { a.setBit0(8) }
    }
}
