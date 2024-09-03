package tech.insight.buffer

import io.netty.buffer.ByteBuf


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/

fun ByteBuf.readUByte() = this.readByte().toUByte()
fun ByteBuf.readUShort() = this.readShort().toUShort()
fun ByteBuf.readUInt() = this.readInt().toUInt()
fun ByteBuf.readULong() = this.readLong().toULong()

fun ByteBuf.readUShortLE() = this.readShortLE().toUShort()
fun ByteBuf.readUIntLE() = this.readIntLE().toUInt()
fun ByteBuf.readULongLE() = this.readLongLE().toULong()


fun ByteBuf.readAllBytes(): ByteArray {
    val allBytes = ByteArray(this.readableBytes())
    this.readBytes(allBytes)
    return allBytes
}
