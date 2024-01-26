package tech.insight.core.extension

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.nio.ByteBuffer


class NumberExtensionKtTest{


    @Test
    fun toBytesTest(){
        val i = 123123
        val expect = ByteBuffer.allocate(Int.SIZE_BYTES).putInt(i).array()
        assertEquals(expect,i.toByteArray())
    }
}
