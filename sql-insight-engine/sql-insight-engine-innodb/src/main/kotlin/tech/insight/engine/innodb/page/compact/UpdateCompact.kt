package tech.insight.engine.innodb.page.compact

import tech.insight.core.bean.NormalRow
import tech.insight.core.bean.UpdateRow
import tech.insight.core.bean.condition.Expression


/**
 *
 * Used to update compact row
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class UpdateCompact(oldRow: Compact, updateFields: MutableMap<String, Expression>) : Compact() {

    init {
        val table = oldRow.belongIndex.table
        variables = Variables.create()
        nullList = CompactNullList.allocate(table)
        recordHeader = RecordHeader.create(RecordType.NORMAL)
        sourceRow = UpdateRow(oldRow.sourceRow, updateFields)
        updateFields.forEach { (colName, expression) ->
            val oldValue = oldRow.getValueByColumnName(colName)
            val newValue = expression.getExpressionValue(oldRow)
            if (oldValue == newValue) {
                return@forEach
            }
            val updateCol = table.columnList.find { it.name == colName }!!
            if (updateCol.notNull) {

            }
        }
    }

    private fun processNull() {

    }


}
