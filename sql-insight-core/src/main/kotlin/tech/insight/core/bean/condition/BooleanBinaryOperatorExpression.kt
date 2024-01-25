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
 * binary operator expression
 * compose left and right
 *
 * @author gongxuanzhangmelt@gmail.com
 */
abstract class BooleanBinaryOperatorExpression protected constructor(
    protected val left: Expression?,
    protected val right: Expression?
) : BooleanExpression {
    /**
     * compare between two expression
     * function params is expression not result.
     * because the boolean can lazy invoke.(circuit logic)
     *
     * @return the function
     */
    protected abstract fun operator(): BooleanOperatorFunction
    override fun getExpressionValue(row: Row?): Value? {
        return ValueBoolean(operator().apply(left, right, row))
    }
}
