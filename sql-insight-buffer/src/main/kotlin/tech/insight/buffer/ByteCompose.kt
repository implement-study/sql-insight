package tech.insight.buffer


/**
 *
 * compose some byte to larger data type
 * param byte1 is the high byte
 * example:
 *  byte1: 0x01
 *  byte2: 0x02
 *  compose short : 0x0102
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
fun compose(byte1: Byte, byte2: Byte): Short {
    return (byte1.toInt() shl 8 or byte2.toInt()).toShort()
}

fun compose(byte1: Byte, byte2: Byte, byte3: Byte, byte4: Byte): Int {
    return (byte1.toInt() shl 24) or
            (byte2.toInt() and 0xff shl 16) or
            (byte3.toInt() and 0xff shl 8) or
            (byte4.toInt() and 0xff)
}

fun compose(
    byte1: Byte,
    byte2: Byte,
    byte3: Byte,
    byte4: Byte,
    byte5: Byte,
    byte6: Byte,
    byte7: Byte,
    byte8: Byte
): Long {
    return (byte1.toLong() shl 56) or
            (byte2.toLong() and 0xff shl 48) or
            (byte3.toLong() and 0xff shl 40) or
            (byte4.toLong() and 0xff shl 32) or
            (byte5.toLong() and 0xff shl 24) or
            (byte6.toLong() and 0xff shl 16) or
            (byte7.toLong() and 0xff shl 8) or
            (byte8.toLong() and 0xff)
}
