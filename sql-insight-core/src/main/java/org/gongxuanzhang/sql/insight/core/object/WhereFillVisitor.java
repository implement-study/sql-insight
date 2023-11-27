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
import com.alibaba.druid.sql.ast.expr.SQLBooleanExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import org.gongxuanzhang.sql.insight.core.object.condition.BooleanCondition;
import org.gongxuanzhang.sql.insight.core.object.condition.Expression;

/**
 * visit a SQLBinaryOpExpr in order get target table info.
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public class WhereFillVisitor implements FillDataVisitor {

    private final WhereContainer whereContainer;

    public WhereFillVisitor(WhereContainer whereContainer) {
        this.whereContainer = whereContainer;
    }


    @Override
    public boolean visit(SQLBinaryOpExpr x) {
        OperatorVisitor visitor = new OperatorVisitor();
        x.accept(visitor);
        return false;
    }

    @Override
    public boolean visit(SQLBooleanExpr x) {
        Where where = new Where(x.getBooleanValue());
        this.whereContainer.setWhere(where);
        return false;
    }

    @Override
    public boolean visit(SQLIntegerExpr x) {
        Where where = new Where(x.getNumber().intValue() != 0);
        this.whereContainer.setWhere(where);
        return false;
    }

    private class OperatorVisitor implements SQLASTVisitor {
        Expression result = null;

        @Override
        public boolean visit(SQLBinaryOpExpr x) {
            OperatorVisitor leftVisitor = new OperatorVisitor();
            OperatorVisitor rightVisitor = new OperatorVisitor();
            x.getLeft().accept(leftVisitor);
            x.getRight().accept(rightVisitor);
            switch (x.getOperator()) {
                case BooleanAnd:
                    new BooleanCondition(true, leftVisitor.result, rightVisitor.result);
                    break;
                case BooleanOr:

                    break;
                default:
                    throw new UnsupportedOperationException(x.getOperator() + "not support");

            }
        }
    }
}

