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

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import org.gongxuanzhang.sql.insight.core.environment.SqlInsightContext;
import org.gongxuanzhang.sql.insight.core.exception.TableNotExistsException;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class TableVisitor implements SQLASTVisitor, TableContainer {

    Table table;

    String databaseName;

    String tableName;

    @Override
    public boolean visit(SQLPropertyExpr x) {
        databaseName = x.getOwnerName();
        tableName = x.getName();
        table = SqlInsightContext.getInstance().getTableDefinitionManager().select(databaseName, tableName);
        return false;
    }

    @Override
    public boolean visit(SQLIdentifierExpr x) {
        tableName = x.getName();
        table = SqlInsightContext.getInstance().getTableDefinitionManager().select(databaseName, tableName);
        return false;
    }

    @Override
    public void postVisit(SQLObject x) {
        if (this.table != null) {
            return;
        }
        Table tempTable = new Table();
        tempTable.setName(tableName);
        if (databaseName != null) {
            tempTable.setDatabase(new Database(databaseName));
        }
        throw new TableNotExistsException(tempTable);
    }

    @Override
    public Table getTable() {
        return table;
    }

    @Override
    public void setTable(Table table) {
        throw new UnsupportedOperationException("table visitor inner table only visa visit");
    }
}
