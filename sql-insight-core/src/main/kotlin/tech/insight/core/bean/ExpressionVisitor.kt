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

import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr
import com.alibaba.druid.sql.ast.expr.SQLCharExpr
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr
import com.alibaba.druid.sql.visitor.SQLASTVisitor
import org.gongxuanzhang.sql.insight.core.`object`.condition.*

/**
 * analysis a expression
 *
 * @author gongxuanzhangmelt@gmail.com
 */
class ExpressionVisitor : SQLASTVisitor {
    private var expression: Expression? = null
    override fun visit(x: SQLBinaryOpExpr): Boolean {
        val leftVisitor = ExpressionVisitor()
        val rightVisitor = ExpressionVisitor()
        x.getLeft().accept(leftVisitor)
        x.getRight().accept(rightVisitor)
        val left: Expression? = leftVisitor.expression
        val right: Expression? = rightVisitor.expression
        when (x.getOperator()) {
            SQLBinaryOperator.BooleanAnd -> expression = AndExpression(left, right)
            SQLBinaryOperator.BooleanOr -> expression = OrExpression(left, right)
            SQLBinaryOperator.LessThan -> expression = LessExpression(left, right)
            SQLBinaryOperator.LessThanOrEqual -> expression = LessEqualsExpression(left, right)
            SQLBinaryOperator.GreaterThan -> expression = GreatExpression(left, right)
            SQLBinaryOperator.GreaterThanOrEqual -> expression = GreatEqualsExpression(left, right)
            SQLBinaryOperator.Equality -> expression = EqualsExpression(left, right)
            SQLBinaryOperator.NotEqual, SQLBinaryOperator.LessThanOrGreater -> expression =
                NotEqualsExpression(left, right)

            SQLBinaryOperator.Add -> expression = AddExpression(left, right)
            SQLBinaryOperator.Subtract -> expression = SubtractExpression(left, right)
            SQLBinaryOperator.Multiply -> expression = PlusExpression(left, right)
            SQLBinaryOperator.Divide -> expression = DivideExpression(left, right)
            else -> throw UnsupportedOperationException("operator [" + x.getOperator() + "] not support")
        }
        return false
    }

    override fun visit(x: SQLIntegerExpr): Boolean {
        expression = IntExpression(x.getNumber().toInt())
        return false
    }

    override fun visit(x: SQLIdentifierExpr): Boolean {
        expression = IdentifierExpression(x.getName())
        return false
    }

    override fun visit(x: SQLCharExpr): Boolean {
        expression = StringExpression(x.getText())
        return false
    }

    override fun visit(x: SQLPropertyExpr): Boolean {
        expression = IdentifierExpression(x.toString())
        return false
    }

    fun getExpression(): Expression? {
        return expression
    }
}
