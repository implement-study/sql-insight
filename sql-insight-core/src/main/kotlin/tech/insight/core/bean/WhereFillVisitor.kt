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
package tech.insight.core.bean

import com.alibaba.druid.sql.ast.expr.SQLBooleanExpr
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr
import org.gongxuanzhang.sql.insight.core.`object`.condition.BooleanExpression

/**
 * visit a SQLBinaryOpExpr in order get target table info.
 *
 * @author gongxuanzhangmelt@gmail.com
 */
class WhereFillVisitor(private val whereContainer: WhereContainer) : FillDataVisitor {
    override fun visit(x: SQLBinaryOpExpr): Boolean {
        val visitor = ExpressionVisitor()
        x.accept(visitor)
        val where: Where = Where(visitor.expression as BooleanExpression)
        whereContainer.setWhere(where)
        return false
    }

    override fun visit(x: SQLBooleanExpr): Boolean {
        val where: Where = Where(x.getBooleanValue())
        whereContainer.setWhere(where)
        return false
    }

    override fun visit(x: SQLIntegerExpr): Boolean {
        val where = Where(x.getNumber().toInt() != 0)
        whereContainer.setWhere(where)
        return false
    }
}
