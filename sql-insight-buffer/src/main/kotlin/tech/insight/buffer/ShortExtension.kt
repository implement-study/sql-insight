package tech.insight.buffer

import kotlin.experimental.and
import kotlin.experimental.or


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/


/**
 * set the index bit to 1,index 0 is the rightmost bit
 */
fun Short.setOne(index: Int): Short {
    checkIndex(index)
    return this or (1 shl index).toShort()
}

/**
 * set the index bit to 0,index 0 is the rightmost bit
 */
fun Short.setZero(index: Int): Short {
    checkIndex(index)
    return this and (1 shl index).inv().toShort()
}

/**
 * get the index bit value is 1,index 0 is the rightmost bit
 */
fun Short.isOne(index: Int): Boolean {
    checkIndex(index)
    val base = 1 shl index
    return this.toInt() and base == base
}

/**
 * get the index bit value is 0,index 0 is the rightmost bit
 */
fun Short.isZero(index: Int): Boolean {
    return !isOne(index)
}

/**
 * split length from the rightmost bit
 * example:
 * the short is 0b01011100,subByte(4) will return 0b1100
 * the short is 0b01011100,subByte(3) will return 0b100
 *
 * @param length must >0 and <16
 */
fun Short.subShort(length: Int): Int {
    return this.subShort(0, length)
}

/**
 * split the byte from the from index to the to index
 * example:
 * the short is 0b01011100,subByte(0,4) will return 0b1100
 * the short is 0b01011100,subByte(1,5) will return 0b1110
 */
fun Short.subShort(from: Int, to: Int): Int {
    checkFromTo(from, to)
    val toBase = (1 shl to) - 1
    return (this.toInt() and toBase) shr from
}

/**
 * cover the bits from the rightmost bit
 * example:
 * the short is 0b01011100,coverBits(0b0101,4) will return 0b0101_0101
 */
fun Short.coverBits(cover: Int, length: Int): Short {
    return this.coverBits(cover, 0, length)
}


fun Short.coverBits(cover: Int, from: Int, to: Int): Short {
    checkFromTo(from, to)
    val toBase = (1 shl to) - 1
    val fromBase = (1 shl from) - 1
    val midBase = toBase xor fromBase
    val thisRemain = midBase.inv() and this.toUShort().toInt()
    val coverRemain = (cover shl from) and midBase
    return (thisRemain or coverRemain).toShort()
}

fun Short.byteArray(): ByteArray {
    return byteArrayOf((this.toInt() shr 8).toByte(), this.toByte())
}

private fun checkFromTo(from: Int, to: Int) {
    require(from in 0..<to) {
        "from must < to"
    }
    require(to <= Short.SIZE_BITS) {
        "to must <=${Short.SIZE_BITS}"
    }
}

private fun checkLength(length: Int) {
    require(length > 0 && length < Short.SIZE_BITS) {
        "length must >0 and <${Short.SIZE_BITS}"
    }
}

private fun checkIndex(index: Int) {
    require(index < Short.SIZE_BITS) {
        "index must be in 0-${Short.SIZE_BITS}"
    }
}
