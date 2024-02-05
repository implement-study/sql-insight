package tech.insight.core.engine.filler

import com.alibaba.druid.sql.ast.SQLObject
import com.alibaba.druid.sql.ast.expr.SQLCharExpr
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr
import com.alibaba.druid.sql.ast.expr.SQLNullExpr
import tech.insight.core.bean.Column
import tech.insight.core.bean.DataType
import tech.insight.core.bean.InsertRow
import tech.insight.core.bean.value.ValueChar
import tech.insight.core.bean.value.ValueInt
import tech.insight.core.bean.value.ValueNull
import tech.insight.core.bean.value.ValueVarchar
import tech.insight.core.exception.InsertException


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class InsertRowFiller(private val insertColumn: List<Column>, private val row: InsertRow) : BeanFiller<InsertRow> {
    private var currentIndex = 0

    init {
        val columnList = row.belongTo().columnList
        columnList.map { it.defaultValue }.forEach { row.valueList.add(it) }
    }

    override fun visit(x: SQLIntegerExpr): Boolean {
        val value = x.number.toInt()
        val currentType = currentColumn().dataType
        if (currentType != DataType.INT) {
            throw InsertException(row.rowId, "number $value can't cast to $currentType")
        }
        row.valueList[currentColumnIndex()] = ValueInt(value)
        return true
    }

    override fun visit(x: SQLCharExpr): Boolean {
        val text = x.text
        val column = currentColumn()
        when (val colType = column.dataType) {
            DataType.VARCHAR -> row.valueList[currentColumnIndex()] =wrapVarchar(text)
            DataType.CHAR -> row.valueList[currentColumnIndex()] = wrapChar(text)
            else -> throw InsertException(row.rowId, "$text can't cast to $colType")
        }
        return true
    }

    override fun visit(x: SQLNullExpr): Boolean {
        row.valueList[currentColumnIndex()] = ValueNull
        return true
    }

    override fun postVisit(x: SQLObject?) {
        currentIndex++
    }

    private fun currentColumn(): Column {
        return insertColumn[currentIndex]
    }

    private fun currentColumnIndex(): Int {
        return row.belongTo().getColumnIndexByName(currentColumn().name)
    }


    private fun wrapVarchar(text: String): ValueVarchar {
        val column = currentColumn()
        val length: Int = column.length
        if (text.length > length) {
            throw InsertException(row.rowId, "Data too long for column ${column.name}")
        }
        return ValueVarchar(text)
    }

    private fun wrapChar(text: String): ValueChar {
        val column = currentColumn()
        val length = column.length
        if (text.toByteArray().size > length) {
            throw InsertException(row.rowId, "Data too long for column ${column.name}")
        }
        return ValueChar(text, length)
    }
}
