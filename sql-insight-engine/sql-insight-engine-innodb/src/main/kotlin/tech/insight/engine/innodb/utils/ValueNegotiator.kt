package tech.insight.engine.innodb.utils

import tech.insight.core.bean.Column
import tech.insight.core.bean.DataType
import tech.insight.core.bean.value.Value
import tech.insight.core.bean.value.ValueInt
import tech.insight.core.bean.value.ValueVarchar
import tech.insight.core.extension.toInt

object ValueNegotiator {

    fun wrapValue(column: Column, value: ByteArray): Value<*> {
        return when (column.dataType) {
            DataType.INT -> ValueInt(value.toInt())
            DataType.VARCHAR, DataType.CHAR -> ValueVarchar(String(value))
            else -> throw IllegalArgumentException()
        }
    }
}
