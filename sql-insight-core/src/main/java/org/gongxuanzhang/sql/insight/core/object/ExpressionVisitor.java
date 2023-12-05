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

package org.gongxuanzhang.sql.insight.core.object;

import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import org.gongxuanzhang.sql.insight.core.object.condition.*;


/**
 * analysis a expression
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public class ExpressionVisitor implements SQLASTVisitor {


    private Expression expression = null;


    @Override
    public boolean visit(SQLBinaryOpExpr x) {
        ExpressionVisitor leftVisitor = new ExpressionVisitor();
        ExpressionVisitor rightVisitor = new ExpressionVisitor();
        x.getLeft().accept(leftVisitor);
        x.getRight().accept(rightVisitor);
        Expression left = leftVisitor.expression;
        Expression right = rightVisitor.expression;
        switch (x.getOperator()) {
            case BooleanAnd:
                this.expression = new AndExpression(left, right);
                break;
            case BooleanOr:
                this.expression = new OrExpression(left, right);
                break;
            case LessThan:
                this.expression = new LessExpression(left, right);
                break;
            case LessThanOrEqual:
                this.expression = new LessEqualsExpression(left, right);
                break;
            case GreaterThan:
                this.expression = new GreatExpression(left, right);
                break;
            case GreaterThanOrEqual:
                this.expression = new GreatEqualsExpression(left, right);
                break;
            case Equality:
                this.expression = new EqualsExpression(left, right);
                break;
            case NotEqual:
            case LessThanOrGreater:
                this.expression = new NotEqualsExpression(left, right);
                break;
            case Add:
                this.expression = new AddExpression(left, right);
                break;
            case Subtract:
                this.expression = new SubtractExpression(left, right);
                break;
            case Multiply:
                this.expression = new PlusExpression(left, right);
                break;
            case Divide:
                this.expression = new DivideExpression(left, right);
                break;
            default:
                throw new UnsupportedOperationException("operator [" + x.getOperator() + "] not support");

        }
        return false;
    }

    @Override
    public boolean visit(SQLIntegerExpr x) {
        this.expression = new IntExpression(x.getNumber().intValue());
        return false;
    }

    @Override
    public boolean visit(SQLIdentifierExpr x) {
        this.expression = new IdentifierExpression(x.getName());
        return false;
    }

    @Override
    public boolean visit(SQLCharExpr x) {
        this.expression = new StringExpression(x.getText());
        return false;
    }

    @Override
    public boolean visit(SQLPropertyExpr x) {
        this.expression = new IdentifierExpression(x.toString());
        return false;
    }

    public Expression getExpression() {
        return this.expression;
    }
}
