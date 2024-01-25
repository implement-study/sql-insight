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

import org.gongxuanzhang.sql.insight.core.`object`.value.Value
import java.util.*

/**
 * @author gongxuanzhangmelt@gmail.com
 */
class ReadRow(valueList: List<Value>, override val rowId: Long) : Row, TableContainer {
    private override var table: Table? = null
    private val valueList: List<Value>

    init {
        this.valueList = valueList
    }

    override val values: List<Any>
        get() = valueList

    override fun getValueByColumnName(colName: String?): Value {
        val columnIndexByName = table!!.getColumnIndexByName(colName)
        return valueList[columnIndexByName!!]
    }

    override fun belongTo(): Table? {
        return table
    }

    override fun getTable(): Table? {
        return belongTo()
    }

    override fun setTable(table: Table?) {
        this.table = table
    }

    override fun toString(): String {
        val stringJoiner = StringJoiner("|", "|", "|")
        for (value in valueList) {
            stringJoiner.add(value.toString())
        }
        return stringJoiner.toString()
    }
}
