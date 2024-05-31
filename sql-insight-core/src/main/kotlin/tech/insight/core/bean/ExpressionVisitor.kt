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
package tech.insight.core.bean

import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator.Add
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator.BooleanAnd
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator.BooleanOr
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator.Divide
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator.Equality
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator.GreaterThan
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator.GreaterThanOrEqual
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator.LessThan
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator.LessThanOrEqual
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator.LessThanOrGreater
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator.Multiply
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator.NotEqual
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator.Subtract
import com.alibaba.druid.sql.ast.expr.SQLCharExpr
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr
import com.alibaba.druid.sql.visitor.SQLASTVisitor
import tech.insight.core.bean.condition.AddExpression
import tech.insight.core.bean.condition.AndExpression
import tech.insight.core.bean.condition.DivideExpression
import tech.insight.core.bean.condition.EqualsExpression
import tech.insight.core.bean.condition.Expression
import tech.insight.core.bean.condition.GreatEqualsExpression
import tech.insight.core.bean.condition.GreatExpression
import tech.insight.core.bean.condition.IdentifierExpression
import tech.insight.core.bean.condition.IntExpression
import tech.insight.core.bean.condition.LessEqualsExpression
import tech.insight.core.bean.condition.LessExpression
import tech.insight.core.bean.condition.NotEqualsExpression
import tech.insight.core.bean.condition.OrExpression
import tech.insight.core.bean.condition.PlusExpression
import tech.insight.core.bean.condition.StringExpression
import tech.insight.core.bean.condition.SubtractExpression

/**
 * analysis a expression
 *
 * @author gongxuanzhangmelt@gmail.com
 */
class ExpressionVisitor(private val expressionAction: (Expression) -> Unit = {}) : SQLASTVisitor {

    private lateinit var expression: Expression

    private val mutableList = mutableListOf<String>()

    override fun visit(x: SQLBinaryOpExpr): Boolean {
        val leftVisitor = ExpressionVisitor()
        val rightVisitor = ExpressionVisitor()
        x.left.accept(leftVisitor)
        x.right.accept(rightVisitor)
        val left: Expression = leftVisitor.expression
        val right: Expression = rightVisitor.expression
        expression = when (x.operator) {
            BooleanAnd -> AndExpression(left, right)
            BooleanOr -> OrExpression(left, right)
            LessThan -> LessExpression(left, right)
            LessThanOrEqual -> LessEqualsExpression(left, right)
            GreaterThan -> GreatExpression(left, right)
            GreaterThanOrEqual -> GreatEqualsExpression(left, right)
            Equality -> EqualsExpression(left, right)
            NotEqual, LessThanOrGreater -> NotEqualsExpression(left, right)
            Add -> AddExpression(left, right)
            Subtract -> SubtractExpression(left, right)
            Multiply -> PlusExpression(left, right)
            Divide -> DivideExpression(left, right)
            else -> throw UnsupportedOperationException("operator [" + x.operator + "] not support")
        }
        expressionAction.invoke(expression)
        return false
    }

    override fun visit(x: SQLIntegerExpr): Boolean {
        expression = IntExpression(x.number.toInt())
        expressionAction.invoke(expression)
        return false
    }

    override fun visit(x: SQLIdentifierExpr): Boolean {
        expression = IdentifierExpression(x.name)
        expressionAction.invoke(expression)
        return false
    }

    override fun visit(x: SQLCharExpr): Boolean {
        expression = StringExpression(x.text)
        expressionAction.invoke(expression)
        return false
    }

    override fun visit(x: SQLPropertyExpr): Boolean {
        expression = IdentifierExpression(x.toString())
        expressionAction.invoke(expression)
        return false
    }

}
