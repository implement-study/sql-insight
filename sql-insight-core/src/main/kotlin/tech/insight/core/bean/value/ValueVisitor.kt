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
package tech.insight.core.bean.value

import com.alibaba.druid.sql.ast.expr.SQLCharExpr
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr
import com.alibaba.druid.sql.ast.expr.SQLNullExpr
import com.alibaba.druid.sql.visitor.SQLASTVisitor

/**
 * visitor a value expr ,work up a [Value],
 * the value can operated after visitor
 *
 * @author gongxuanzhangmelt@gmail.com
 */
class ValueVisitor(private val valueAction: (Value<*>) -> Unit) : SQLASTVisitor {

    override fun endVisit(x: SQLCharExpr) {
        valueAction.invoke(ValueVarchar(x.text))
    }

    override fun endVisit(x: SQLIntegerExpr) {
        valueAction.invoke(ValueInt(x.number.toInt()))
    }

    override fun endVisit(x: SQLNullExpr) {
        valueAction.invoke(ValueNull)
    }
}
