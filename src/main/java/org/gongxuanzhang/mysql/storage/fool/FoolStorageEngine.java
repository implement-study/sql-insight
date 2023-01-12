package org.gongxuanzhang.mysql.storage.fool;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.mysql.annotation.Engine;
import org.gongxuanzhang.mysql.core.Condition;
import org.gongxuanzhang.mysql.core.Result;
import org.gongxuanzhang.mysql.entity.DeleteInfo;
import org.gongxuanzhang.mysql.entity.InsertInfo;
import org.gongxuanzhang.mysql.entity.SelectInfo;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.entity.UpdateInfo;
import org.gongxuanzhang.mysql.exception.ExecuteException;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.storage.StorageEngine;
import org.gongxuanzhang.mysql.tool.Context;
import org.gongxuanzhang.mysql.tool.DbFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import static org.gongxuanzhang.mysql.tool.ExceptionThrower.errorSwap;

/**
 * 傻子引擎，只有功能完全没有性能。
 * 为了搭建最基础的内容
 * 同时也展示了如果不优化，查询效率是有多低!
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Engine
@Slf4j
public class FoolStorageEngine implements StorageEngine {


    @Override
    public String getEngineName() {
        return "fool";
    }

    @Override
    public boolean supportTransaction() {
        return false;
    }

    @Override
    public Result createTable(TableInfo tableInfo) throws MySQLException {
        File gfrmFile = tableInfo.sourceFile();
        try {
            if (gfrmFile.exists() || !gfrmFile.createNewFile()) {
                throw new ExecuteException("表" + tableInfo.getTableName() + "已经存在");
            }
        } catch (IOException e) {
            return errorSwap(e);
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(gfrmFile);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(tableInfo);
            log.info("创建表{}.{}", tableInfo.getDatabase(), tableInfo.getTableName());
            Context.getTableManager().register(tableInfo);
            return Result.success();
        } catch (IOException e) {
            return errorSwap(e);
        }
    }

    @Override
    public Result insert(InsertInfo info) throws MySQLException {
        return null;
    }

    @Override
    public Result delete(DeleteInfo info) throws MySQLException {
        return null;
    }

    @Override
    public Result update(UpdateInfo info) throws MySQLException {
        return null;
    }

    @Override
    public Result select(SelectInfo info) throws MySQLException {
        return null;
    }


}
