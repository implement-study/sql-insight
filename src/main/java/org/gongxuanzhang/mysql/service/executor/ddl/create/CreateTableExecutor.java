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

package org.gongxuanzhang.mysql.service.executor.ddl.create;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.CreateTableInfo;
import org.gongxuanzhang.mysql.entity.page.InnoDbPage;
import org.gongxuanzhang.mysql.entity.page.InnoDbPageFactory;
import org.gongxuanzhang.mysql.entity.page.PageType;
import org.gongxuanzhang.mysql.exception.ExecuteException;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.executor.ddl.DdlExecutor;
import org.gongxuanzhang.mysql.tool.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;

import static org.gongxuanzhang.mysql.tool.ExceptionThrower.errorSwap;

/**
 * 创建表
 * create table
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
public class CreateTableExecutor extends DdlExecutor<CreateTableInfo> {


    public CreateTableExecutor(CreateTableInfo info) {
        super(info);
    }

    @Override
    public Result doExecute(CreateTableInfo tableInfo) throws MySQLException {
        if (tableInfo.notIfExists() && tableInfo.structFile().exists()) {
            log.info("表{}已经存在", tableInfo.getTableName());
            return Result.success();
        }
        checkTableFile(tableInfo.structFile());
        checkTableFile(tableInfo.dataFile());
        try (FileOutputStream fileOutputStream = new FileOutputStream(tableInfo.structFile());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(tableInfo);
            File dataFile = tableInfo.dataFile();
            InnoDbPageFactory innoDbPageFactory = InnoDbPageFactory.getInstance();
            InnoDbPage rootPage = innoDbPageFactory.create();
            rootPage.getFileHeader().setSpaceId(tableInfo.getSpaceId());
            rootPage.getFileHeader().setPageType(PageType.FIL_PAGE_INDEX.getValue());
            byte[] pageBytes = rootPage.toBytes();
            Files.write(dataFile.toPath(), pageBytes);
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
