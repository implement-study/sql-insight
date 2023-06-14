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

package org.gongxuanzhang.mysql.storage.fool;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.ExecuteException;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.storage.CreateTableEngine;
import org.gongxuanzhang.mysql.tool.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import static org.gongxuanzhang.mysql.tool.ExceptionThrower.errorSwap;

/**
 * fool 引擎的 create table
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
public class FoolTableCreator implements CreateTableEngine {
    @Override
    public Result createTable(TableInfo tableInfo) throws MySQLException {
        checkTableFile(tableInfo.structFile());
        checkTableFile(tableInfo.dataFile());
        try (FileOutputStream fileOutputStream = new FileOutputStream(tableInfo.structFile());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(tableInfo);
            log.info("创建表{}.{}", tableInfo.getDatabase().getDatabaseName(), tableInfo.getTableName());
            Context.getTableManager().register(tableInfo);
            return Result.success();
        } catch (IOException e) {
            return errorSwap(e);
        }
    }

    private void checkTableFile(File file) throws MySQLException {
        try {
            if (file.exists() || !file.createNewFile()) {
                String name = file.getName();
                throw new ExecuteException("表" + name.substring(0, name.indexOf(".")) + "已经存在");
            }
        } catch (IOException e) {
            errorSwap(e);
        }
    }
}
