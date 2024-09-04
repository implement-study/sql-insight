package tech.insight.buffer


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
fun ByteArray.toInt(): Int {
    require(this.size == Integer.BYTES) { "byte array length must be " + Integer.BYTES }
    return this[0].toInt() and 0xFF shl 24 or
            (this[1].toInt() and 0xFF shl 16) or
            (this[2].toInt() and 0xFF shl 8) or
            (this[3].toInt() and 0xFF)
}
