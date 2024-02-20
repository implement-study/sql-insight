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

import tech.insight.core.bean.value.Value

/**
 * @author gongxuanzhangmelt@gmail.com
 */
class InsertRow(override val rowId: Long) : Row, SQLBean, Iterable<InsertItem> {
    lateinit var table: Table

    /**
     * all table column value，not just columns in SQL
     */
    val valueList: MutableList<Value<*>> = ArrayList()

    override val values: MutableList<Value<*>>
        get() = valueList

    override fun getValueByColumnName(colName: String): Value<*> {
        val index = table.getColumnIndexByName(colName)
        return valueList[index]
    }

    override fun belongTo(): Table {
        return table
    }

    override fun iterator(): Iterator<InsertItem> {
        return Iter()
    }

    private inner class Iter : Iterator<InsertItem> {
        var cursor = 0
        override fun hasNext(): Boolean {
            return cursor != valueList.size
        }

        override fun next(): InsertItem {
            val i = cursor
            if (i >= valueList.size) {
                throw NoSuchElementException()
            }
            cursor = i + 1
            return InsertItem(table.columnList[i], valueList[i])
        }
    }

}

data class InsertItem(val column: Column, val value: Value<*>)
