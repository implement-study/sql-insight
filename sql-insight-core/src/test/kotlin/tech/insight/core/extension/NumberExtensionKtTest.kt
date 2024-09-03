package tech.insight.core.extension

import java.nio.ByteBuffer
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test


class NumberExtensionKtTest {


    @Test
    fun toBytesTest() {
        val i = 123123
        val expect = ByteBuffer.allocate(Int.SIZE_BYTES).putInt(i).array()
        assertArrayEquals(expect, i.toByteArray())
    }
}
