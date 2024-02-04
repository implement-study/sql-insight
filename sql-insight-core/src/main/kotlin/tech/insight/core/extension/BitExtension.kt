package tech.insight.core.extension


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class BitMapBuilder {
    private var origin: Int = 0
    private var length: Int = 0

    fun toByte(): Byte {
        return origin.toByte()
    }

    infix fun append(that: Boolean): BitMapBuilder {
        require(length < 8) { IllegalArgumentException("bitmap overflow") }
        origin = origin shl 1 or (if (that) 1 else 0)
        return this
    }

}


infix fun Boolean.append(that: Boolean): BitMapBuilder {
    return BitMapBuilder().append(this).append(that)
}
