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
