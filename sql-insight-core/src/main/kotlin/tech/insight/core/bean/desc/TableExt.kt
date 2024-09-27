package tech.insight.core.bean.desc

import tech.insight.core.bean.Column
import tech.insight.core.bean.SQLBean
import tech.insight.core.bean.Table

/**
 *
 * table extension info
 *
 * @author gongxuanzhangmelt@gmail.com
 */
class TableExt(val table: Table) : SQLBean {

    val columnMap: MutableMap<String, Column> = HashMap()
    val columnIndex: MutableMap<String, Int> = HashMap()

    /**
     * not null column index list
     */
    val notNullIndex: MutableList<Int> = ArrayList()
    val variableIndex: MutableList<Int> = ArrayList()
    var autoColIndex = -1
    var primaryKeyIndex = -1
    var nullableColCount = 0
    var primaryKeyName: String? = null


    init {
        table.columnList.forEachIndexed { index, col ->
            columnMap[col.name] = col
            columnIndex[col.name] = index
            if (col.primaryKey) {
                this.primaryKeyIndex = index
                this.primaryKeyName = col.name
            }
            if (col.autoIncrement) {
                require(autoColIndex == -1) { "auto increment column can have at most one" }
                autoColIndex = index
            }
            if (col.notNull) {
                notNullIndex.add(index)
            } else {
                this.nullableColCount++
            }
            if (col.variable) {
                this.variableIndex.add(index)
            }
        }
    }

    override fun parent(): SQLBean {
        return table
    }
}
