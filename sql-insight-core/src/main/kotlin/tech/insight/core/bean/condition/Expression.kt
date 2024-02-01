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
package tech.insight.core.bean.condition

import tech.insight.core.bean.Row
import tech.insight.core.bean.SQLBean
import tech.insight.core.bean.value.*


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
     * value to boolean support expression
     */
    fun getBooleanValue(row: Row): Boolean {
        return when (val value = getExpressionValue(row)) {
            is ValueBoolean -> value.source
            is ValueChar -> value.source.isNotEmpty()
            is ValueInt -> value.source >= 1
            is ValueNull -> false
            is ValueVarchar -> value.source.isNotEmpty()
        }
    }
}
