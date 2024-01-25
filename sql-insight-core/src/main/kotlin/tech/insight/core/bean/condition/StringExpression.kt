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
 * there are two ways to result values.
 * fixed value or get column value
 *
 * @author gongxuanzhangmelt@gmail.com
 */
class StringExpression(private val value: String) : Expression {
    override fun getExpressionValue(row: Row?): ValueVarchar? {
        return ValueVarchar(value)
    }
}
