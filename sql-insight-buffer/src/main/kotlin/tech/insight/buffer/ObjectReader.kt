package tech.insight.buffer


/**
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
fun interface ObjectReader<T> {

    fun readObject(bytes: ByteArray): T
}
