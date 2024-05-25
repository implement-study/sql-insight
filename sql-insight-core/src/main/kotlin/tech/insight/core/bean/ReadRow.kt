/*
 * Copyright 2023 sql-insight  and the original author or authors <gongxuanzhangmelt@gmail.com>.
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
import tech.insight.core.util.truncateStringIfTooLong
import java.util.*

/**
 * @author gongxuanzhangmelt@gmail.com
 */
class ReadRow(valueList: List<Value<*>>, override val rowId: Long) : Row {
    lateinit var table: Table
    private val valueList: List<Value<*>>

    init {
        this.valueList = valueList
    }

    override val values: List<Value<*>>
        get() = valueList

    override fun getValueByColumnName(colName: String): Value<*> {
        val columnIndexByName = table.getColumnIndexByName(colName)
        return valueList[columnIndexByName]
    }

    override fun belongTo(): Table {
        return table
    }


    override fun toString(): String {
        val stringJoiner = StringJoiner("|", "|", "|")
        for (value in valueList) {
            stringJoiner.add(truncateStringIfTooLong(value.toString(), 10))
        }
        return stringJoiner.toString()
    }
}
