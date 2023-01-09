package org.gongxuanzhang.mysql.service.executor.ddl.drop;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.mysql.core.Result;
import org.gongxuanzhang.mysql.entity.DatabaseInfo;
import org.gongxuanzhang.mysql.exception.ExecuteException;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.tool.ContextSupport;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

/**
 * 删除数据库
 * drop database database_name
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
public class DropDatabaseExecutor implements Executor {


    private final DatabaseInfo databaseInfo;

    public DropDatabaseExecutor(DatabaseInfo databaseInfo) {
        this.databaseInfo = databaseInfo;
    }


    @Override
    public Result doExecute() throws MySQLException {
        if (!ContextSupport.getDatabases().contains(databaseInfo.getDatabaseName())) {
            String message = String.format("数据库%s不存在", databaseInfo.getDatabaseName());
            throw new ExecuteException(message);
        }
        File databaseHome = ContextSupport.getDatabaseHome(databaseInfo.getDatabaseName());
        try {
            Files.walk(databaseHome.toPath())
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            throw new ExecuteException("删除数据库失败");
        }
        ContextSupport.refreshDatabases();
        log.info("删除{}数据库", databaseInfo.getDatabaseName());
        return Result.success();
    }
}
