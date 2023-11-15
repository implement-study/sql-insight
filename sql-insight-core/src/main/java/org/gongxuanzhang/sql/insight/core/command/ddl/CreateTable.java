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

package org.gongxuanzhang.sql.insight.core.command.ddl;

import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.fastjson2.JSONObject;
import org.gongxuanzhang.sql.insight.core.annotation.Temporary;
import org.gongxuanzhang.sql.insight.core.environment.ExecuteContext;
import org.gongxuanzhang.sql.insight.core.environment.SqlInsightContext;
import org.gongxuanzhang.sql.insight.core.exception.DatabaseNotExistsException;
import org.gongxuanzhang.sql.insight.core.exception.TableExistsException;
import org.gongxuanzhang.sql.insight.core.object.Table;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class CreateTable implements CreateCommand {

    private final String sql;

    private Table table;

    private boolean ifNotExists;


    public CreateTable(String sql) {
        this.sql = sql;
    }


    @Override
    public void run(ExecuteContext context) throws Exception {
        File dbFolder = table.getDatabase().getDbFolder();
        if (!dbFolder.exists() || !dbFolder.isDirectory()) {
            throw new DatabaseNotExistsException(table.getDatabase().getName());
        }
        SqlInsightContext sqlInsightContext = SqlInsightContext.getInstance();
        File frmFile = new File(dbFolder, this.table.getName() + ".frm");
        if (frmFile.createNewFile()) {
            Files.write(frmFile.toPath(), tableFrmByteArray());
            sqlInsightContext.selectEngine(table.getEngine()).createTable(table);
            sqlInsightContext.getTableDefinitionManager().load(table);
            return;
        }
        if (!this.ifNotExists) {
            throw new TableExistsException(table);
        }

    }


    @Temporary(detail = "use json temp")
    private byte[] tableFrmByteArray() {
        return JSONObject.toJSONString(this.table).getBytes();
    }


    public boolean isIfNotExists() {
        return ifNotExists;
    }

    public Table getTable() {
        return table;
    }

    @Override
    public void endVisit(SQLCreateTableStatement x) {
        this.table = new Table();
        this.ifNotExists = x.isIfNotExists();
        x.accept(this.table);
    }

    @NotNull
    @Override
    public String getSql() {
        return this.sql;
    }

}
