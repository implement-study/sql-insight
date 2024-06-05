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
 * operator expression function
 *
 * @author gongxuanzhangmelt@gmail.com
 */

interface BooleanExpression : Expression {
    /**
     *
     * return boolean from a row
     * @param row
     */
    override fun getExpressionValue(row: Row): Value<Boolean>

}

interface BooleanOperatorFunction : BooleanExpression {
    /**
     * a operator
     *
     * @param left  left expression
     * @param right right expression
     * @param row   a row
     * @return result value
     */
    fun process(left: Expression, right: Expression, row: Row): Boolean
}


sealed class BaseBooleanExpression(protected val left: Expression, protected val right: Expression) :
    BooleanOperatorFunction {

    override fun getExpressionValue(row: Row): Value<Boolean> {
        val result = process(left, right, row)
        return ValueBoolean(result)
    }

    abstract override fun process(left: Expression, right: Expression, row: Row): Boolean
}


class AndExpression(left: Expression, right: Expression) : BaseBooleanExpression(left, right) {

    private var impossible = false

    private val identifiers = mutableSetOf<String>()

    init {
        if (left.impossible() || right.impossible()) {
            this.impossible = true
        }
        identifiers.addAll(left.identifierNames())
        identifiers.addAll(right.identifierNames())
    }


    override fun impossible(): Boolean {
        return impossible
    }

    override fun identifierNames(): Set<String> {
        return identifiers
    }

    override fun originExpressionString(): String {
        return "(${left.originExpressionString()}) AND (${right.originExpressionString()})"
    }

    override fun process(left: Expression, right: Expression, row: Row): Boolean {
        if (this.impossible()) {
            return false
        }
        return left.getBooleanValue(row) && right.getBooleanValue(row)
    }

}

class OrExpression(left: Expression, right: Expression) : BaseBooleanExpression(left, right) {

    private var impossible = false

    private val identifiers = mutableSetOf<String>()

    init {
        if (left.impossible() && right.impossible()) {
            this.impossible = true
        }
        identifiers.addAll(left.identifierNames())
        identifiers.addAll(right.identifierNames())
    }


    override fun process(left: Expression, right: Expression, row: Row): Boolean {
        return left.getBooleanValue(row) || right.getBooleanValue(row)
    }

    override fun identifierNames(): Set<String> {
        return identifiers
    }

    override fun originExpressionString(): String {
        return "(${left.originExpressionString()}) OR (${right.originExpressionString()})"
    }

}




