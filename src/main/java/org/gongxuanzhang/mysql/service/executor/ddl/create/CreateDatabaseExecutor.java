/*
 * Copyright 2023 java-mysql  and the original author or authors <gongxuanzhangmelt@gmail.com>.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/implement-study/java-mysql/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gongxuanzhang.mysql.service.executor.ddl.create;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.mysql.core.MySqlProperties;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.CreateDatabaseInfo;
import org.gongxuanzhang.mysql.entity.GlobalProperties;
import org.gongxuanzhang.mysql.exception.ExecuteException;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.exception.SqlParseException;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.tool.Context;
import org.gongxuanzhang.mysql.tool.SqlUtils;

import java.io.File;

/**
 * 创建数据库
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
public class CreateDatabaseExecutor implements Executor {


    private final CreateDatabaseInfo databaseInfo;

    public CreateDatabaseExecutor(CreateDatabaseInfo databaseInfo) {
        this.databaseInfo = databaseInfo;
    }


    @Override
    public Result doExecute() throws MySQLException {
        String databaseName = databaseInfo.getDatabaseName();
        try {
            SqlUtils.checkVarName(databaseName);
        } catch (SqlParseException e) {
            throw new ExecuteException(e.getMessage());
        }
        String dataDir = GlobalProperties.getInstance().get(MySqlProperties.DATA_DIR);
        File db = new File(dataDir);
        File file = new File(db, databaseName);
        if (file.exists()) {
            if (databaseInfo.notIfExists()) {
                log.info("数据库{}已经存在", databaseName);
                return Result.success();
            }
            throw new ExecuteException("数据库" + databaseName + "已经存在");
        }
        file.mkdirs();
        log.info("创建{}数据库", databaseName);
        Context.getDatabaseManager().register(databaseInfo);
        return Result.success();
    }
}
