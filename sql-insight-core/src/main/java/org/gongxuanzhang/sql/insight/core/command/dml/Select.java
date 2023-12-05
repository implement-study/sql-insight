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

import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import org.gongxuanzhang.sql.insight.core.analysis.SqlType;
import org.gongxuanzhang.sql.insight.core.annotation.Temporary;
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
        return false;
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
}
