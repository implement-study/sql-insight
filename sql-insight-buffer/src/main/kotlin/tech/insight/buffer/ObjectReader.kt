package tech.insight.buffer


/**
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
fun interface ObjectReader<out T> {

    fun readObject(bytes: ByteArray): T
}
