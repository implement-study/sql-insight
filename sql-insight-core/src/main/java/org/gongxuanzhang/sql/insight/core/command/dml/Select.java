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

package org.gongxuanzhang.sql.insight.core.command.dml;

import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import org.gongxuanzhang.sql.insight.core.analysis.SqlType;
import org.gongxuanzhang.sql.insight.core.annotation.Temporary;
import org.gongxuanzhang.sql.insight.core.object.Limit;
import org.gongxuanzhang.sql.insight.core.object.OrderBy;
import org.gongxuanzhang.sql.insight.core.object.Table;
import org.gongxuanzhang.sql.insight.core.object.TableVisitor;
import org.gongxuanzhang.sql.insight.core.object.Where;
import org.gongxuanzhang.sql.insight.core.object.WhereContainer;
import org.gongxuanzhang.sql.insight.core.object.WhereFillVisitor;
import org.gongxuanzhang.sql.insight.core.optimizer.plan.ExecutionPlan;
import org.gongxuanzhang.sql.insight.core.optimizer.plan.SelectExecutionPlan;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class Select implements DmlCommand, WhereContainer {

    private final String sql;

    private List<Table> tableList;

    private Where where;

    private OrderBy orderBy;

    private Limit limit;

    public Select(String sql) {
        this.sql = sql;
    }

    @Override
    public ExecutionPlan plan() {
        return new SelectExecutionPlan(this);
    }

    @Override
    public boolean visit(SQLSelectQueryBlock x) {
        tableList = new ArrayList<>();
        x.getFrom().accept(new FromVisitor());
        x.getWhere().accept(new WhereFillVisitor(this));
        if (x.getOrderBy() != null) {
            x.getOrderBy().accept(new OrderByVisitor());
        }
        if (x.getLimit() != null) {
            x.getLimit().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(SQLLimit x) {
        IntegerVisitor visitor = new IntegerVisitor();
        this.limit = new Limit();
        if (x.getOffset() != null) {
            x.getOffset().accept(visitor);
            limit.setSkip(visitor.value);
        }
        x.getRowCount().accept(visitor);
        limit.setRowCount(visitor.value);
        return false;
    }

    private static class IntegerVisitor implements SQLASTVisitor {

        int value = 0;

        @Override
        public void endVisit(SQLIntegerExpr x) {
            this.value = x.getNumber().intValue();
        }
    }


    private class OrderByVisitor implements SQLASTVisitor {

        @Override
        public boolean visit(SQLOrderBy x) {
            String[] orderByColumnNames = new String[x.getItems().size()];
            boolean[] asc = new boolean[orderByColumnNames.length];
            List<SQLSelectOrderByItem> items = x.getItems();
            for (int i = 0; i < items.size(); i++) {
                SQLSelectOrderByItem item = items.get(i);
                if (item.getType() == SQLOrderingSpecification.ASC) {
                    asc[i] = true;
                }
                orderByColumnNames[i] = item.getExpr().toString();
            }
            Select.this.orderBy = new OrderBy(orderByColumnNames, asc);
            return false;
        }

    }

    private class FromVisitor implements SQLASTVisitor {


        @Temporary(detail = "how to deal join condition?")
        @Override
        public boolean visit(SQLJoinTableSource x) {
            x.getLeft().accept(this);
            x.getRight().accept(this);
            return false;
        }

        @Override
        public boolean visit(SQLExprTableSource x) {
            TableVisitor tableVisitor = new TableVisitor();
            x.accept(tableVisitor);
            Select.this.tableList.add(tableVisitor.getTable());
            return false;
        }
    }

    @NotNull
    @Override
    public String getSql() {
        return this.sql;
    }

    @NotNull
    @Override
    public SqlType getSqlType() {
        return SqlType.DELETE;
    }


    public List<Table> getTableList() {
        return tableList;
    }

    @Override
    public Where getWhere() {
        return this.where;
    }

    @Override
    public void setWhere(Where where) {
        this.where = where;
    }

    public Limit getLimit() {
        return limit;
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

}
