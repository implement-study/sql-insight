package tech.insight.engine.innodb.page

import tech.insight.core.bean.value.Value


/**
 *
 * innodb index node key,different index contains different key
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
interface IndexKey


open class SingleKey(val value: Value<*>, val unique: Boolean) : IndexKey

/**
 * primary key
 */
class PrimaryKey(value: Value<*>) : SingleKey(value, true)


class MultiColumnKey(values: Array<Value<*>>)
