/*
 * Copyright 2023 sql-insight  and the original author or authors <gongxuanzhangmelt@gmail.com>.
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

package org.gongxuanzhang.mysql.service.executor.ddl.drop;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.mysql.core.manager.DatabaseManager;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.DatabaseInfo;
import org.gongxuanzhang.mysql.entity.DropDatabaseInfo;
import org.gongxuanzhang.mysql.exception.ExecuteException;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.tool.Context;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * 删除数据库
 * drop database database_name
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
public class DropDatabaseExecutor implements Executor {


    private final DropDatabaseInfo databaseInfo;

    public DropDatabaseExecutor(DropDatabaseInfo databaseInfo) {
        this.databaseInfo = databaseInfo;
    }


    @Override
    public Result doExecute() throws MySQLException {
        DatabaseManager databaseManager = Context.getDatabaseManager();
        DatabaseInfo select = databaseManager.select(databaseInfo.getDatabaseName());
        if (select == null || !select.sourceFile().exists()) {
            String message = String.format("数据库%s不存在", databaseInfo.getDatabaseName());
            throw new ExecuteException(message);
        }

        try (Stream<Path> walk = Files.walk(select.sourceFile().toPath())) {
            walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            databaseManager.refresh();
            log.info("删除{}数据库", databaseInfo.getDatabaseName());
            Context.getDatabaseManager().remove(databaseInfo.getDatabaseName());
            Context.getTableManager().removeDatabase(databaseInfo.getDatabaseName());
            return Result.success();
        } catch (IOException e) {
            throw new ExecuteException("删除数据库失败");
        }
    }
}
