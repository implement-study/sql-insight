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

import com.alibaba.druid.sql.ast.expr.SQLCharExpr
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr
import com.alibaba.druid.sql.ast.expr.SQLNullExpr
import lombok.Data
import tech.insight.core.bean.value.*
import tech.insight.core.exception.InsertException

/**
 * @author gongxuanzhangmelt@gmail.com
 */
class InsertRow(val insertColumns: List<Column>, override val rowId: Long) : Row,  SQLBean,
    Iterable<InsertItem?> {
    lateinit var table: Table
     val valueList: MutableList<Value<*>> = ArrayList()

    /**
     * all table column value
     */
    private val absoluteValueList: MutableList<Value<*>> = ArrayList()

    override val values: List<Any>
        get() = valueList

    override fun getValueByColumnName(colName: String): Value<*> {
        val index = table.getColumnIndexByName(colName)
        return getAbsoluteValueList()[index]
    }

    override fun belongTo(): Table {
        return table
    }


    /**
     * return current visit values target column.
     * use before current add.
     */
    private fun currentColumn(): Column {
        return insertColumns[valueList.size]
    }

    fun getAbsoluteValueList(): List<Value<*>> {
        if (absoluteValueList.isEmpty()) {
            for (i in 0 until table.getColumnList().size()) {
                absoluteValueList.add(ValueNull.getInstance())
            }
            for (i in insertColumns.indices) {
                val current = insertColumns[i]
                val columnIndexByName = table.getColumnIndexByName(current.getName())
                absoluteValueList[columnIndexByName] = valueList[i]
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
