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
import tech.insight.core.bean.value.Value
import tech.insight.core.bean.value.ValueBoolean


/**
 * calc two value to one value.
 * can combine differ value type
 *
 * @author gongxuanzhangmelt@gmail.com
 */
sealed class ValueOperatorExpression(private val left: Expression, private val right: Expression) : Expression {
    /**
     * calculate a result from left and right value
     *
     * @return the function
     */
    protected abstract fun operator(left: Value<*>, right: Value<*>): Value<*>

    override fun getExpressionValue(row: Row): Value<*> {
        return operator(left.getExpressionValue(row), right.getExpressionValue(row))
    }
}

class AddExpression(left: Expression, right: Expression) : ValueOperatorExpression(left, right) {
    override fun operator(left: Value<*>, right: Value<*>): Value<*> {
        return left + right
    }
}

class DivideExpression(left: Expression, right: Expression) : ValueOperatorExpression(left, right) {
    override fun operator(left: Value<*>, right: Value<*>): Value<*> {
        return left / right
    }
}

class PlusExpression(left: Expression, right: Expression) : ValueOperatorExpression(left, right) {
    override fun operator(left: Value<*>, right: Value<*>): Value<*> {
        return left * right
    }
}

class SubtractExpression(left: Expression, right: Expression) : ValueOperatorExpression(left, right) {
    override fun operator(left: Value<*>, right: Value<*>): Value<*> {
        return left - right
    }
}

class GreatExpression(left: Expression, right: Expression) : ValueOperatorExpression(left, right) {
    override fun operator(left: Value<*>, right: Value<*>): Value<*> {
        return ValueBoolean(left > right)
    }
}

class GreatEqualsExpression(left: Expression, right: Expression) : ValueOperatorExpression(left, right) {
    override fun operator(left: Value<*>, right: Value<*>): Value<*> {
        return ValueBoolean(left >= right)
    }
}

class LessExpression(left: Expression, right: Expression) : ValueOperatorExpression(left, right) {
    override fun operator(left: Value<*>, right: Value<*>): Value<*> {
        return ValueBoolean(left < right)
    }
}

class LessEqualsExpression(left: Expression, right: Expression) : ValueOperatorExpression(left, right) {
    override fun operator(left: Value<*>, right: Value<*>): Value<*> {
        return ValueBoolean(left <= right)
    }
}


class EqualsExpression(left: Expression, right: Expression) : ValueOperatorExpression(left, right) {
    override fun operator(left: Value<*>, right: Value<*>): Value<*> {
        return ValueBoolean(left == right)
    }
}

class NotEqualsExpression(left: Expression, right: Expression) : ValueOperatorExpression(left, right) {
    override fun operator(left: Value<*>, right: Value<*>): Value<*> {
        return ValueBoolean(left != right)
    }
}




