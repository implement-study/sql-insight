package tech.insight.buffer

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.nio.charset.Charset


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


fun Boolean.byteArray() = byteArrayOf(if (this) 1 else 0)

fun ByteBuf.readAllBytes(): ByteArray {
    val allBytes = ByteArray(this.readableBytes())
    this.readBytes(allBytes)
    return allBytes
}

fun ByteBuf.getAllBytes(): ByteArray {
    val allBytes = ByteArray(this.readableBytes())
    this.getBytes(0, allBytes)
    return allBytes
}

fun ByteBuf.writeLengthAndString(value: String?): ByteBuf {
    if (value == null) {
        return this.writeInt(-1)
    }
    val byteArray = value.toByteArray(Charset.defaultCharset())
    return this.writeInt(byteArray.size).writeBytes(byteArray)
}


fun ByteBuf.writeCollection(collection: Collection<SerializableObject>): ByteBuf {
    this.writeInt(collection.size)
    collection.forEach {
        this.writeObject(it)
    }
    return this
}


fun <T> ByteBuf.readCollection(readConvert: (ByteArray) -> T): List<T> {
    val size = this.readInt()
    val list = mutableListOf<T>()
    repeat(size) {
        readConvert(readLengthAndBytes()).let { list.add(it) }
    }
    return list
}

fun ByteBuf.writeObject(o: SerializableObject): ByteBuf {
    return this.writeLengthAndBytes(o.toBytes())
}

fun <T> ByteBuf.readObject(readConvert: (ByteArray) -> T): T {
    this.readLengthAndBytes().let {
        return readConvert(it)
    }
}

fun ByteBuf.readLengthAndString(): String? {
    val length = readInt()
    if (length == -1) {
        return null
    }
    return readCharSequence(length, Charset.defaultCharset()).toString()
}

fun ByteBuf.writeLengthAndBytes(bytes: ByteArray): ByteBuf {
    return writeInt(bytes.size).writeBytes(bytes)
}

fun ByteBuf.readLengthAndBytes(): ByteArray {
    return readLength(readInt())

}

fun ByteBuf.getLength(index: Int, length: Int): ByteArray {
    val bytes = ByteArray(length)
    getBytes(index,bytes)
    return bytes
}

fun ByteBuf.readLength(length: Int): ByteArray {
    val bytes = ByteArray(length)
    this.readBytes(bytes)
    return bytes
}

fun byteBuf(length: Int = -1): ByteBuf {
    if (length <= 0) {
        return Unpooled.buffer()
    }
    return Unpooled.buffer(length)
}

fun wrappedBuf(array: ByteArray): ByteBuf {
    return Unpooled.wrappedBuffer(array)
}

fun copyBuf(array: ByteArray): ByteBuf {
    return Unpooled.copiedBuffer(array)
}
