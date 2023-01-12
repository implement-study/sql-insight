package org.gongxuanzhang.mysql.service.executor.ddl.drop;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.mysql.core.Result;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.ExecuteException;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.executor.Executor;

import java.io.File;

/**
 * 删除表
 * drop table tableName
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
public class DropTableExecutor implements Executor {


    private final TableInfo tableInfo;

    public DropTableExecutor(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }


    @Override
    public Result doExecute() throws MySQLException {
        File gfrmFile = this.tableInfo.structFile();
        if (!gfrmFile.exists()) {
            String message = String.format("表%s.%s不存在", tableInfo.getDatabase(), tableInfo.getTableName());
            throw new ExecuteException(message);
        }
        if (!gfrmFile.delete()) {
            String message = String.format("删除表%s.%s失败", tableInfo.getDatabase(), tableInfo.getTableName());
            throw new ExecuteException(message);
        }
        log.info("删除表{}.{}", tableInfo.getDatabase(), tableInfo.getTableName());
        return Result.success();
    }
}
