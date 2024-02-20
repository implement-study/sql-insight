package tech.insight.core.bean

import tech.insight.core.bean.value.Value


/**
 * base row
 *
 * @author gongxuanzhangmelt@gmail.com
 */
interface Row : Comparable<Row> {
    /**
     * values in row
     *
     * @return row list
     */
    val values: List<Value<*>>

    /**
     * row id
     */
    val rowId: Long

    /**
     * get value by col name,if row don't have the column,throw exception
     *
     * @param colName column name
     * @return value or error
     */
    fun getValueByColumnName(colName: String): Value<*>

    /**
     * the row related table
     *
     * @return table
     */
    fun belongTo(): Table


    /**
     * default implement is compare id of the both
     */
    override fun compareTo(other: Row): Int {
        return rowId.compareTo(other.rowId)
    }
}
