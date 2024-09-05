package tech.insight.buffer


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
fun Int.byteArray(): ByteArray {
    return ByteArray(4) { i ->
        ((this shr ((3 - i) * 8)) and 0xFF).toByte()
    }
}
