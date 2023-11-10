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

package org.gongxuanzhang.sql.insight.core.analysis.druid;

import com.alibaba.druid.sql.ast.statement.SQLCreateDatabaseStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropDatabaseStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import org.gongxuanzhang.sql.insight.core.command.Command;
import org.gongxuanzhang.sql.insight.core.command.CommandContainer;
import org.gongxuanzhang.sql.insight.core.command.ddl.CreateDatabase;
import org.gongxuanzhang.sql.insight.core.command.ddl.CreateTable;
import org.gongxuanzhang.sql.insight.core.command.ddl.DropDatabase;

/**
 * druid visitor, created a command fill field.
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public class DruidAnalyzerAdaptor implements SQLASTVisitor, CommandContainer {

    private final String sql;

    private Command command;

    public DruidAnalyzerAdaptor(String sql) {
        this.sql = sql;
    }


    @Override
    public boolean visit(SQLCreateDatabaseStatement x) {
        this.command = new CreateDatabase(sql, x.isIfNotExists(), x.getDatabaseName());
        return true;
    }


    @Override
    public boolean visit(SQLDropDatabaseStatement x) {
        this.command = new DropDatabase(sql);
        x.accept(this.command);
        return false;
    }


    @Override
    public boolean visit(SQLCreateTableStatement x) {
        this.command = new CreateTable(sql);
        x.accept(this.command);
        return true;
    }






    @Override
    public Command getCommand() {
        return this.command;
    }
}