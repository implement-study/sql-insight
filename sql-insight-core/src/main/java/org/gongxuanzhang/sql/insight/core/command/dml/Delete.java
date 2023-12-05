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

import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import org.gongxuanzhang.sql.insight.core.analysis.SqlType;
import org.gongxuanzhang.sql.insight.core.object.Table;
import org.gongxuanzhang.sql.insight.core.object.TableContainer;
import org.gongxuanzhang.sql.insight.core.object.TableFillVisitor;
import org.gongxuanzhang.sql.insight.core.object.Where;
import org.gongxuanzhang.sql.insight.core.object.WhereContainer;
import org.gongxuanzhang.sql.insight.core.object.WhereFillVisitor;
import org.gongxuanzhang.sql.insight.core.optimizer.plan.DeleteExecutionPlan;
import org.gongxuanzhang.sql.insight.core.optimizer.plan.ExecutionPlan;
import org.jetbrains.annotations.NotNull;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class Delete implements DmlCommand, TableContainer, WhereContainer {

    private final String sql;

    private Table table;

    private Where where;

    public Delete(String sql) {
        this.sql = sql;
    }

    @Override
    public ExecutionPlan plan() {
        return new DeleteExecutionPlan(this);
    }

    @Override
    public boolean visit(SQLDeleteStatement x) {
        x.getTableSource().accept(new TableFillVisitor(this));
        x.getWhere().accept(new WhereFillVisitor(this));
        return false;
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

    @Override
    public Table getTable() {
        return this.table;
    }

    @Override
    public void setTable(Table table) {
        this.table = table;
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