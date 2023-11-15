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

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import org.gongxuanzhang.sql.insight.core.analysis.SqlType;
import org.gongxuanzhang.sql.insight.core.command.Command;
import org.gongxuanzhang.sql.insight.core.exception.InsertException;
import org.gongxuanzhang.sql.insight.core.object.Column;
import org.gongxuanzhang.sql.insight.core.object.InsertRow;
import org.gongxuanzhang.sql.insight.core.object.Row;
import org.gongxuanzhang.sql.insight.core.object.Table;
import org.gongxuanzhang.sql.insight.core.object.TableContainer;
import org.gongxuanzhang.sql.insight.core.object.TableFillVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * visit table source
 * visit columns
 * visit values clause
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public class Insert implements Command, TableContainer {


    private final String sql;

    private Table table;

    private final List<Column> insertColumns;

    private final List<Row> insertRows;

    public Insert(String sql) {
        this.sql = sql;
        this.insertRows = new ArrayList<>();
        this.insertColumns = new ArrayList<>();
    }


    @Override
    public boolean visit(SQLInsertStatement x) {
        x.getTableSource().accept(new TableFillVisitor(this));
        ColumnVisitor columnVisitor = new ColumnVisitor();
        x.getColumns().forEach(col -> col.accept(columnVisitor));
        ValuesClauseVisitor valueVisitor = new ValuesClauseVisitor();
        x.getValuesList().forEach(vc -> vc.accept(valueVisitor));
        return true;
    }

    /**
     * visit values clause must after visit table because insert row should have complete table info before visit
     * values clause
     **/
    private class ValuesClauseVisitor implements SQLASTVisitor {
        int rowIndex = 1;

        @Override
        public void endVisit(SQLInsertStatement.ValuesClause x) {
            if (x.getValues().size() != insertColumns.size()) {
                throw new InsertException(rowIndex, "Column count doesn't match value count");
            }
            InsertRow row = new InsertRow(insertColumns, rowIndex++);
            insertRows.add(row);
            x.accept(row);
        }
    }


    private class ColumnVisitor implements SQLASTVisitor {

        Set<String> rowNameSet = new HashSet<>();

        @Override
        public void endVisit(SQLIdentifierExpr x) {
            String colName = x.getName();
            if (!rowNameSet.add(colName)) {
                throw new InsertException("Column " + colName + " specified twice");
            }
            insertColumns.add(table.getColumnByName(colName));
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
        return SqlType.INSERT;
    }

    @Override
    public Table getTable() {
        return this.table;
    }

    @Override
    public void setTable(Table table) {
        this.table = table;
    }

    public List<Row> getInsertRows() {
        return insertRows;
    }

    public List<Column> getInsertColumns() {
        return insertColumns;
    }
}
