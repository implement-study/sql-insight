package tech.insight.core.extension


fun Long.toByteArray(): ByteArray {
    return ByteArray(8) { i ->
        ((this shr (i * 8)) and 0xFF).toByte()
    }
}

fun Int.toByteArray(): ByteArray {
    return ByteArray(4) { i ->
        ((this shr ((3 - i) * 8)) and 0xFF).toByte()
    }
}

fun Short.toByteArray(): ByteArray {
    return ByteArray(2) { i ->
        ((this.toInt() shr (i * 8)) and 0xFF).toByte()
    }
}

fun Byte.toByteArray(): ByteArray {
    return ByteArray(2) { i ->
        ((this.toInt() shr (i * 8)) and 0xFF).toByte()
    }
}

fun Boolean.toByteArray(): ByteArray {
    return byteArrayOf(if (this) 1 else 0)
}

fun ByteArray.toInt(): Int {
    require(this.size == Integer.BYTES) { "byte array length must be " + Integer.BYTES }
    return this[0].toInt() and 0xFF shl 24 or
            (this[1].toInt() and 0xFF shl 16) or
            (this[2].toInt() and 0xFF shl 8) or
            (this[3].toInt() and 0xFF)
}

