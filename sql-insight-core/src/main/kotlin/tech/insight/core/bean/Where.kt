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

import tech.insight.core.bean.condition.BooleanExpression
import tech.insight.core.bean.condition.Expression
import tech.insight.core.bean.value.Value
import tech.insight.core.bean.value.ValueBoolean
import tech.insight.core.bean.value.ValueFalse
import tech.insight.core.bean.value.ValueTrue

/**
 * @author gongxuanzhangmelt@gmail.com
 */
open class Where(val condition: Expression) : BooleanExpression {

    lateinit var table: Table

    override fun getExpressionValue(row: Row): ValueBoolean {
        return ValueBoolean(condition.getBooleanValue(row))
    }

}

object Always : Where(object : BooleanExpression {
    override fun getExpressionValue(row: Row): Value<Boolean> {
        return ValueTrue
    }
})

object Never : Where(object : BooleanExpression {
    override fun getExpressionValue(row: Row): Value<Boolean> {
        return ValueFalse
    }
})


