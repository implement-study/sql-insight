package org.gongxuanzhang.mysql.service.executor.ddl.create;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.mysql.core.Result;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.ExecuteException;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.tool.DbFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import static org.gongxuanzhang.mysql.tool.ExceptionThrower.errorSwap;

/**
 * 创建表
 * create table
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
public class CreateTableExecutor implements Executor {


    private final TableInfo tableInfo;

    public CreateTableExecutor(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }


    @Override
    public Result doExecute() throws MySQLException {
        File gfrmFile = DbFactory.getGfrmFile(this.tableInfo);
        try {
            if (gfrmFile.exists() || !gfrmFile.createNewFile()) {
                throw new ExecuteException("表" + tableInfo.getTableName() + "已经存在");
            }
        } catch (IOException e) {
            return errorSwap(e);
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(gfrmFile);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(this.tableInfo);
            log.info("创建表{}.{}", tableInfo.getDatabase(), tableInfo.getTableName());
            return Result.success();
        } catch (IOException e) {
            return errorSwap(e);
        }
    }
}
