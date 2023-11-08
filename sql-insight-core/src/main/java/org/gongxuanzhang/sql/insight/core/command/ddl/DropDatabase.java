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

import org.gongxuanzhang.sql.insight.core.environment.ExecuteContext;
import org.gongxuanzhang.sql.insight.core.exception.DatabaseNotExistsException;
import org.gongxuanzhang.sql.insight.core.exception.RuntimeIoException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class DropDatabase implements DropCommand {

    private final boolean ifIsExists;

    private final String dbName;

    private final String sql;


    public DropDatabase(String sql, boolean ifIsExists, String dbName) {
        this.ifIsExists = ifIsExists;
        this.dbName = dbName;
        this.sql = sql;
    }


    public boolean getIfIsExists() {
        return ifIsExists;
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
        if (dbFold.exists()) {
            deleteAllFiles(dbFold.toPath());
            return;
        }
        if (!ifIsExists) {
            throw new DatabaseNotExistsException(this.dbName);
        }
    }

    public void deleteAllFiles(Path directory) {
        try (Stream<Path> walk = Files.walk(directory)) {
            walk.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            throw new RuntimeIoException(e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeIoException(e);
        }
    }


    private File getDbFold(ExecuteContext context) {
        //  todo get path in context
        return new File(this.dbName);
    }
}
