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

package org.gongxuanzhang.mysql.service.executor.ddl.drop;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.ExecuteException;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.executor.ddl.BatchDdlExecutor;
import org.gongxuanzhang.mysql.tool.Context;

import java.io.File;
import java.util.List;

/**
 * 删除表
 * drop table tableName
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
public class DropTableExecutor extends BatchDdlExecutor<TableInfo> {


    public DropTableExecutor(List<TableInfo> infos) {
        super(infos);
    }

    @Override
    public Result doExecute(List<TableInfo> infos) throws MySQLException {
        for (TableInfo info : infos) {
            dropTable(info);
        }
        return Result.success();
    }


    public void dropTable(TableInfo tableInfo) throws MySQLException {
        File gfrmFile = tableInfo.structFile();
        String databaseName = tableInfo.getDatabase().getDatabaseName();
        if (!gfrmFile.exists()) {
            String message = String.format("表%s.%s不存在", databaseName, tableInfo.getTableName());
            throw new ExecuteException(message);
        }
        if (!gfrmFile.delete()) {
            String message = String.format("删除表%s.%s失败", databaseName, tableInfo.getTableName());
            throw new ExecuteException(message);
        }
        File dataFile = tableInfo.dataFile();
        if (!dataFile.delete()) {
            String message = String.format("删除表%s.%s失败", databaseName, tableInfo.getTableName());
            throw new ExecuteException(message);
        }
        log.info("删除表{}.{}", databaseName, tableInfo.getTableName());
        Context.getTableManager().remove(String.format("%s.%s", databaseName, tableInfo.getTableName()));
    }

}
