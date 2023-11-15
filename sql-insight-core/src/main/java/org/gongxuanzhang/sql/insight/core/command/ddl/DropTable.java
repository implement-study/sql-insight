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

import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.sql.insight.core.engine.storage.StorageEngine;
import org.gongxuanzhang.sql.insight.core.environment.ExecuteContext;
import org.gongxuanzhang.sql.insight.core.environment.SqlInsightContext;
import org.gongxuanzhang.sql.insight.core.exception.RuntimeIoException;
import org.gongxuanzhang.sql.insight.core.exception.TableNotExistsException;
import org.gongxuanzhang.sql.insight.core.object.Table;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
@Slf4j
public class DropTable implements DropCommand {

    private final String sql;

    private final List<Table> dropTables;

    private boolean ifExists;


    public DropTable(String sql) {
        this.sql = sql;
        this.dropTables = new ArrayList<>();
    }


    @Override
    public void run(ExecuteContext context) throws Exception {
        SqlInsightContext insightContext = SqlInsightContext.getInstance();
        this.dropTables.forEach(table -> {
            File dbFolder = table.getDatabase().getDbFolder();
            File frmFile = new File(dbFolder, table.getName() + ".frm");
            try {
                if (Files.deleteIfExists(frmFile.toPath())) {
                    StorageEngine engine = insightContext.selectEngine(table.getEngine());
                    for (String ext : engine.tableExtensions()) {
                        Files.deleteIfExists(new File(dbFolder, table.getName() + "." + ext).toPath());
                    }
                    return;
                }
            } catch (IOException e) {
                throw new RuntimeIoException(e);
            }
            insightContext.getTableDefinitionManager().unload(table);
            if (!this.ifExists) {
                throw new TableNotExistsException(table);
            }
        });
    }


    public boolean isIfExists() {
        return ifExists;
    }


    @Override
    public void endVisit(SQLDropTableStatement x) {
        this.ifExists = x.isIfExists();
        x.getTableSources().forEach(sqlExprTableSource -> {
            Table dropped = new Table();
            this.dropTables.add(dropped);
            sqlExprTableSource.accept(dropped);
        });
    }


    @NotNull
    @Override
    public String getSql() {
        return this.sql;
    }

}
