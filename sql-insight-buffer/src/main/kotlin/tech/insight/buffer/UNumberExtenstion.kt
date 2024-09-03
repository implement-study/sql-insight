package tech.insight.buffer


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
operator fun UShort.times(int: Int) = this.toInt() * int

operator fun UShort.minus(int: Int) = this.toInt() - int

operator fun UShort.plus(int: Int) = (this + int.toUShort()).toUShort()

operator fun Int.minus(uShort: UShort) = this - uShort.toInt()

operator fun UShort.compareTo(int: Int) = this.toInt().compareTo(int)
