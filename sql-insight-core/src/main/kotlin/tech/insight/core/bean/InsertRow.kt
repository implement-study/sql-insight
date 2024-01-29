/*
 * Copyright 2023 java-mysql  and the original author or authors <gongxuanzhangmelt@gmail.com>.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/implement-study/sql-insight/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.insight.core.bean

import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr
import com.alibaba.druid.sql.ast.expr.SQLNullExpr
import lombok.Data
import tech.insight.core.bean.value.ValueChar
import tech.insight.core.bean.value.ValueVarchar

/**
 * @author gongxuanzhangmelt@gmail.com
 */
class InsertRow(val insertColumns: List<Column>, override val rowId: Long) : Row, FillDataVisitor, TableContainer,
    Iterable<InsertItem?> {
    override var table: Table? = null
    private val valueList: MutableList<Value> = ArrayList<Value>()

    /**
     * all table column value
     */
    private val absoluteValueList: MutableList<Value> = ArrayList<Value>()

    override val values: List<Any>
        get() = valueList

    override fun getValueByColumnName(colName: String?): Value {
        val index = table!!.getColumnIndexByName(colName)
        return getAbsoluteValueList()[index!!]
    }

    override fun belongTo(): Table? {
        return table
    }

    override fun endVisit(x: SQLIntegerExpr) {
        val value: Int = x.getNumber().toInt()
        val currentType: DataType.Type = currentColumn().getDataType().getType()
        if (currentType != DataType.Type.INT) {
            throw InsertException(rowId, "number $value can't cast to $currentType")
        }
        valueList.add(ValueInt(value))
    }

    @Temporary(detail = "instead to negotiate")
    override fun endVisit(x: SQLCharExpr) {
        val text: String = x.getText()
        val column = currentColumn()
        val dataType: DataType = column.getDataType()
        when (dataType.getType()) {
            VARCHAR -> valueList.add(wrapVarchar(text))
            CHAR -> valueList.add(wrapChar(text))
            else -> throw InsertException(rowId, text + " can't cast to " + dataType.getType())
        }
    }

    override fun endVisit(x: SQLNullExpr) {
        valueList.add(ValueNull.getInstance())
    }

    private fun wrapVarchar(text: String): ValueVarchar {
        val column = currentColumn()
        val length: Int = column.getDataType().getLength()
        if (text.length > length) {
            throw InsertException(rowId, "Data too long for column " + column.getName())
        }
        return ValueVarchar(text)
    }

    private fun wrapChar(text: String): ValueChar {
        val column = currentColumn()
        val length: Int = column.getDataType().getLength()
        if (text.toByteArray().size > length) {
            throw InsertException(rowId, "Data too long for column " + column.getName())
        }
        return ValueChar(text, length)
    }

    /**
     * return current visit values target column.
     * use before current add.
     */
    private fun currentColumn(): Column {
        return insertColumns[valueList.size]
    }

    fun getAbsoluteValueList(): List<Value> {
        if (absoluteValueList.isEmpty()) {
            for (i in 0 until table.getColumnList().size()) {
                absoluteValueList.add(ValueNull.getInstance())
            }
            for (i in insertColumns.indices) {
                val current = insertColumns[i]
                val columnIndexByName = table!!.getColumnIndexByName(current.getName())
                absoluteValueList[columnIndexByName!!] = valueList[i]
            }
        }
        return absoluteValueList
    }

    override fun iterator(): MutableIterator<InsertItem> {
        return Iter()
    }

    private inner class Iter : MutableIterator<InsertItem> {
        var cursor = 0
        override fun hasNext(): Boolean {
            return cursor != absoluteValueList.size
        }

        override fun next(): InsertItem {
            val i = cursor
            if (i >= absoluteValueList.size) {
                throw NoSuchElementException()
            }
            cursor = i + 1
            val insertItem = InsertItem()
            insertItem.column = table.getColumnList().get(i)
            insertItem.value = absoluteValueList[i]
            return insertItem
        }
    }

    @Data
    class InsertItem {
        var column: Column? = null
        var value: Value? = null
    }
}
