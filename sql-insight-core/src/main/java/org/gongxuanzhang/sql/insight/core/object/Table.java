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

import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import lombok.Getter;
import org.gongxuanzhang.sql.insight.core.analysis.druid.CommentVisitor;
import org.gongxuanzhang.sql.insight.core.exception.UnknownColumnException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
@Getter
public final class Table implements FillDataVisitor, CommentContainer {

    private Database database;

    private String name;

    private final List<Column> columnList = new ArrayList<>();

    private String comment;

    private String engine;

    private final Map<String, Column> columnMap = new HashMap<>();

    private int autoColIndex = -1;


    public Column getColumnByName(String name) {
        Column column = columnMap.get(name);
        if (column == null) {
            throw new UnknownColumnException(name);
        }
        return column;
    }

    @Override
    public void endVisit(SQLColumnDefinition x) {
        Column column = new Column();
        x.accept(column);
        this.columnList.add(column);
        if (column.isAutoIncrement()) {
            if (this.autoColIndex != -1) {
                throw new UnsupportedOperationException("only support single column autoincrement");
            }
            this.autoColIndex = columnList.size() - 1;
        }
        this.columnMap.put(column.getName(), column);
    }


    @Override
    public boolean visit(SQLCreateTableStatement x) {
        if (x.getComment() != null) {
            x.getComment().accept(new CommentVisitor(this));
        }
        if (x.getEngine() != null) {
            x.getEngine().accept(new EngineVisitor());
        }
        return true;
    }

    @Override
    public boolean visit(SQLExprTableSource x) {
        x.accept(new NameVisitor());
        return true;
    }

    public class EngineVisitor implements SQLASTVisitor {

        @Override
        public void endVisit(SQLCharExpr x) {
            Table.this.engine = x.getText();
        }
    }


    private class NameVisitor implements SQLASTVisitor {

        @Override
        public boolean visit(SQLPropertyExpr x) {
            Table.this.database = new Database(x.getOwnerName());
            Table.this.name = x.getName();
            return false;
        }

        @Override
        public boolean visit(SQLIdentifierExpr x) {
            Table.this.name = x.getName();
            return false;
        }
    }

    @Override
    public Table setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public Table setName(String name) {
        this.name = name;
        return this;
    }

    public Table setDatabase(Database database) {
        this.database = database;
        return this;
    }
}
