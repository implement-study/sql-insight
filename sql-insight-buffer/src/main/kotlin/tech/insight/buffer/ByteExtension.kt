package tech.insight.buffer

import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/


/**
 * set the index bit to 1,index 0 is the rightmost bit
 */
fun Byte.setOne(index: Int): Byte {
    checkIndex(index)
    return this or (1 shl index).toByte()
}

/**
 * set the index bit to 0,index 0 is the rightmost bit
 */
fun Byte.setZero(index: Int): Byte {
    checkIndex(index)
    return this and (1 shl index).inv().toByte()
}

/**
 * get the index bit value is 1,index 0 is the rightmost bit
 */
fun Byte.isOne(index: Int): Boolean {
    checkIndex(index)
    val base = 1 shl index
    return this.toInt() and base == base
}

/**
 * get the index bit value is 0,index 0 is the rightmost bit
 */
fun Byte.isZero(index: Int): Boolean {
    return !isOne(index)
}

/**
 * split length from the rightmost bit
 * example:
 * the byte is 0b01011100,subByte(4) will return 0b1100
 * the byte is 0b01011100,subByte(3) will return 0b100
 *
 * @param length must >0 and <8
 */
fun Byte.subByte(length: Int): Int {
    return this.subByte(0, length)
}

/**
 * split the byte from the from index to the to index
 * example:
 * the byte is 0b01011100,subByte(0,4) will return 0b1100
 * the byte is 0b01011100,subByte(1,5) will return 0b1110
 */
fun Byte.subByte(from: Int, to: Int): Int {
    require(from in 0..<to && to <= Byte.SIZE_BITS) {
        "from must < to and to must <=8"
    }
    val toBase = (1 shl to) - 1
    return (this.toInt() and toBase) shr from
}

/**
 * cover the bits from the rightmost bit
 * example:
 * the byte is 0b01011100,coverBits(0b0101,4) will return 0b0101_0101
 */
fun Byte.coverBits(cover: Int, length: Int = Byte.SIZE_BITS): Byte {
    checkLength(length)
    if (length == Byte.SIZE_BITS) {
        return (cover and 0xff).toByte()
    }
    val base = ((1 shl length) - 1)
    val left = this and (base.toByte().inv())
    val right = (cover and base).toByte()
    return left or right
}

private fun checkLength(length: Int) {
    require(length > 0 && length <= Byte.SIZE_BITS) {
        "length must >0 and <=${Byte.SIZE_BITS}"
    }
}

private fun checkIndex(index: Int) {
    require(index < Byte.SIZE_BITS) {
        "index must be in 0-7"
    }
}
