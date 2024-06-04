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
package tech.insight.core.bean.condition

import tech.insight.core.bean.Row
import tech.insight.core.bean.SQLBean
import tech.insight.core.bean.value.Value
import tech.insight.core.bean.value.ValueBoolean
import tech.insight.core.bean.value.ValueChar
import tech.insight.core.bean.value.ValueInt
import tech.insight.core.bean.value.ValueNull
import tech.insight.core.bean.value.ValueVarchar


/**
 * you can calculate the result with row
 *
 * @author gongxuanzhangmelt@gmail.com
 */
interface Expression : SQLBean {
    /**
     * expression and row  calculate
     *
     * @return value
     */
    fun getExpressionValue(row: Row): Value<*>


    /**
     * the expression is impossible, in other words, the expression getBoolean Value is always false
     */
    fun impossible(): Boolean = false

    /**
     * the expression contains  identifiers that must be satisfied.
     * for example, "a = 1 and b = 3", the identifiers is a and b because a and b have same importance
     *  "a = 1 or b = 3" return empty list,because a and b neither necessarily satisfied
     * support optimizer
     */
    fun identifiers(): List<String>

    /**
     * before analyze expression
     */
    fun originExpressionString(): String

    
    /**
     * value to boolean support expression
     */
    fun getBooleanValue(row: Row): Boolean {
        if (impossible()) {
            return false
        }
        return when (val value = getExpressionValue(row)) {
            is ValueBoolean -> value.source
            is ValueChar -> value.source.isNotEmpty()
            is ValueInt -> value.source >= 1
            is ValueNull -> false
            is ValueVarchar -> value.source.isNotEmpty()
        }
    }
}
