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


    protected val identifiers = mutableListOf<String>()

    protected abstract val operatorType: OperatorType


    init {
        identifiers.addAll(left.identifiers())
        identifiers.addAll(right.identifiers())
    }

    /**
     * calculate a result from left and right value
     *
     * @return the function
     */
    protected abstract fun operator(left: Value<*>, right: Value<*>): Value<*>

    override fun getExpressionValue(row: Row): Value<*> {
        return operator(left.getExpressionValue(row), right.getExpressionValue(row))
    }

    override fun identifiers(): List<String> {
        return identifiers
    }

}

class AddExpression(left: Expression, right: Expression) : ValueOperatorExpression(left, right) {

    override val operatorType: OperatorType = OperatorType.ADD

    override fun operator(left: Value<*>, right: Value<*>): Value<*> {
        return left + right
    }

}

class DivideExpression(left: Expression, right: Expression) : ValueOperatorExpression(left, right) {

    override val operatorType: OperatorType = OperatorType.DIVIDE

    override fun operator(left: Value<*>, right: Value<*>): Value<*> {
        return left / right
    }
}

class PlusExpression(left: Expression, right: Expression) : ValueOperatorExpression(left, right) {

    override val operatorType: OperatorType = OperatorType.PLUS

    override fun operator(left: Value<*>, right: Value<*>): Value<*> {
        return left * right
    }
}

class SubtractExpression(left: Expression, right: Expression) : ValueOperatorExpression(left, right) {

    override val operatorType: OperatorType = OperatorType.SUBTRACT

    override fun operator(left: Value<*>, right: Value<*>): Value<*> {
        return left - right
    }
}

class GreatExpression(left: Expression, right: Expression) : ValueOperatorExpression(left, right) {

    override val operatorType: OperatorType = OperatorType.GREAT_THAN

    override fun operator(left: Value<*>, right: Value<*>): Value<*> {
        return ValueBoolean(left > right)
    }
}

class GreatEqualsExpression(left: Expression, right: Expression) : ValueOperatorExpression(left, right) {

    override val operatorType: OperatorType = OperatorType.GREAT_THAN_OR_EQUAL

    override fun operator(left: Value<*>, right: Value<*>): Value<*> {
        return ValueBoolean(left >= right)
    }
}

class LessExpression(left: Expression, right: Expression) : ValueOperatorExpression(left, right) {

    override val operatorType: OperatorType = OperatorType.LESS_THAN

    override fun operator(left: Value<*>, right: Value<*>): Value<*> {
        return ValueBoolean(left < right)
    }
}

class LessEqualsExpression(left: Expression, right: Expression) : ValueOperatorExpression(left, right) {

    override val operatorType: OperatorType = OperatorType.LESS_THAN_OR_EQUAL

    override fun operator(left: Value<*>, right: Value<*>): Value<*> {
        return ValueBoolean(left <= right)
    }
}


class EqualsExpression(left: Expression, right: Expression) : ValueOperatorExpression(left, right) {

    override val operatorType: OperatorType = OperatorType.EQUAL

    override fun operator(left: Value<*>, right: Value<*>): Value<*> {
        return ValueBoolean(left == right)
    }
}

class NotEqualsExpression(left: Expression, right: Expression) : ValueOperatorExpression(left, right) {

    override val operatorType: OperatorType = OperatorType.NOT_EQUAL

    override fun operator(left: Value<*>, right: Value<*>): Value<*> {
        return ValueBoolean(left != right)
    }
}




