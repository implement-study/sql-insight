package tech.insight.core.extension


fun Long.toByteArray(): ByteArray {
    return ByteArray(8) { i ->
        ((this shr (i * 8)) and 0xFF).toByte()
    }
}

fun Int.toByteArray(): ByteArray {
    return ByteArray(4) { i ->
        ((this shr ((3-i) * 8)) and 0xFF).toByte()
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


