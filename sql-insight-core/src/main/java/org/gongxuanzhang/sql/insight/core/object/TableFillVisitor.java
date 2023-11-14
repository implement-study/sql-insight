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

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import org.gongxuanzhang.sql.insight.core.environment.SqlInsightContext;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class TableFillVisitor implements FillDataVisitor {

    private final TableContainer tableContainer;

    public TableFillVisitor(TableContainer tableContainer) {
        this.tableContainer = tableContainer;
    }

    @Override
    public boolean visit(SQLExprTableSource x) {
        NameVisitor nameVisitor = new NameVisitor();
        x.accept(nameVisitor);
        this.tableContainer.setTable(nameVisitor.table);
        return true;
    }


    private static class NameVisitor implements SQLASTVisitor {

        Table table;

        @Override
        public boolean visit(SQLPropertyExpr x) {
            String databaseName = x.getOwnerName();
            String tableName = x.getName();
            table = SqlInsightContext.getInstance().getTableDefinitionManager().select(databaseName, tableName);
            return false;
        }

        @Override
        public boolean visit(SQLIdentifierExpr x) {
            String tableName = x.getName();
            table = SqlInsightContext.getInstance().getTableDefinitionManager().select(null, tableName);
            return false;
        }
    }


}
