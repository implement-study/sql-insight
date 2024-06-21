package tech.insight.engine.innodb.page.compact

import tech.insight.core.bean.condition.Expression


/**
 *
 * Used to update compact row
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class UpdateCompact(oldRow: Compact, updateFields: MutableMap<String, Expression>) : Compact() {

//    init {
//        updateFields.forEach { (colName, expression) ->
//            val oldValue = oldRow.getValueByColumnName(colName)
//            val newValue = expression.getExpressionValue(oldRow)
//            if (oldValue == newValue) {
//                return@forEach
//            }
//            val table = oldRow.belongIndex.table
//            table.columnList.forEach { col->
//                if(){
//                    
//                }
//            }
//        }
//    }

}
