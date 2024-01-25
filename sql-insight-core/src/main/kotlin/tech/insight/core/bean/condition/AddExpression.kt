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

import org.gongxuanzhang.sql.insight.core.`object`.value.ValueInt

/**
 * @author gongxuanzhangmelt@gmail.com
 */
class AddExpression(left: Expression, right: Expression) : ValueOperatorExpression(left, right) {
    override fun operator(): ValueOperatorFunction {
        return ValueOperatorFunction { left: Value?, right: Value? ->
            if (left is ValueNull || right is ValueNull) {
                throw UnsupportedOperationException("Null can't '+' ")
            }
            if (left is ValueVarchar || right is ValueVarchar) {
                return@ValueOperatorFunction ValueVarchar(left.getSource().toString() + right.getSource().toString())
            }
            val result: Int = (left as ValueInt?).getSource() + (right as ValueInt?).getSource()
            ValueInt(result)
        }
    }
}
