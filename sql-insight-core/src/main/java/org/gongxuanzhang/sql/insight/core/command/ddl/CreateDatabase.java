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

import org.gongxuanzhang.sql.insight.core.environment.DefaultProperty;
import org.gongxuanzhang.sql.insight.core.environment.ExecuteContext;
import org.gongxuanzhang.sql.insight.core.exception.DatabaseExistsException;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class CreateDatabase implements CreateCommand {

    private final boolean ifNotExists;

    private final String dbName;

    private final String sql;


    public CreateDatabase(String sql, boolean ifNotExists, String dbName) {
        this.ifNotExists = ifNotExists;
        this.dbName = dbName;
        this.sql = sql;
    }


    public boolean getIfNotExists() {
        return ifNotExists;
    }

    public String getDbName() {
        return dbName;
    }

    @NotNull
    @Override
    public String getSql() {
        return this.sql;
    }


    @Override
    public void run(ExecuteContext context) {
        File dbFold = getDbFold(context);
        if (dbFold.exists() && !ifNotExists) {
            throw new DatabaseExistsException(this.dbName);
        }
        dbFold.mkdirs();
    }

    private File getDbFold(ExecuteContext context) {
        String home = context.get(DefaultProperty.DATA_DIR.getKey());
        return new File(home, this.dbName);
    }
}
