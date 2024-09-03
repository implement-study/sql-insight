package tech.insight.buffer

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test


class ComposeKtTest {

    @Test
    fun testCompose() {
        assertEquals(0xf102.toShort(), compose(0xf1.toByte(), 0x02.toByte()))
        assertEquals(0xffff.toShort(), compose(0xff.toByte(), 0xff.toByte()))
        assertEquals(0x1234_5678, compose(0x12.toByte(), 0x34.toByte(), 0x56.toByte(), 0x78.toByte()))
        assertEquals(
            0x1234_5678_9abc_def0,
            compose(
                0x12.toByte(), 0x34.toByte(), 0x56.toByte(), 0x78.toByte(),
                0x9a.toByte(), 0xbc.toByte(), 0xde.toByte(), 0xf0.toByte()
            )
        )
    }
}
