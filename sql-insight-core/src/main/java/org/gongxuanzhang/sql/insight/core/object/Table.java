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
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class Table implements FillDataVisitor {

    private Database database;

    private String name;

    private final List<Column> columnList = new ArrayList<>();


    @Override
    public void endVisit(SQLColumnDefinition x) {
        Column column = new Column();
        x.accept(column);
        this.columnList.add(column);
    }


    @Override
    public boolean visit(SQLExprTableSource x) {
        x.accept(new SQLASTVisitor() {
            @Override
            public boolean visit(SQLPropertyExpr x) {
                Table.this.database = new Database(name);
                Table.this.name = x.getName();
                return false;
            }

            @Override
            public boolean visit(SQLIdentifierExpr x) {
                Table.this.name = x.getName();
                return false;
            }
        });
        return true;
    }

    public Database getDatabase() {
        return database;
    }

    public String getName() {
        return name;
    }

    public List<Column> getColumnList() {
        return columnList;
    }
}
