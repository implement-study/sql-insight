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

import org.gongxuanzhang.sql.insight.core.`object`.Row

/**
 * calc two value to one value.
 * must be number
 *
 * @author gongxuanzhangmelt@gmail.com
 */
abstract class NumberOperatorExpression protected constructor(
    protected val left: Expression,
    protected val right: Expression
) : Expression {
    /**
     * calculate a result from left and right value
     *
     * @return the function
     */
    protected abstract fun operator(): NumberValueOperatorFunction
    protected abstract fun operatorDesc(): Char
    override fun getExpressionValue(row: Row?): Value? {
        val leftValue: Value? = left.getExpressionValue(row)
        val rightValue: Value? = right.getExpressionValue(row)
        if (leftValue !is ValueInt || rightValue !is ValueInt) {
            throw UnsupportedOperationException(operatorDesc().toString() + " must be pair of number ")
        }
        return operator().apply(leftValue as ValueInt?, rightValue as ValueInt?)
    }
}
