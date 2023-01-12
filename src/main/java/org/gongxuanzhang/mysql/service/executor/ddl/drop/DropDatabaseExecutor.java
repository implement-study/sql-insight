package org.gongxuanzhang.mysql.service.executor.ddl.drop;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.mysql.core.Result;
import org.gongxuanzhang.mysql.core.manager.DatabaseManager;
import org.gongxuanzhang.mysql.entity.DatabaseInfo;
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


    private final DatabaseInfo databaseInfo;

    public DropDatabaseExecutor(DatabaseInfo databaseInfo) {
        this.databaseInfo = databaseInfo;
    }


    @Override
    public Result doExecute() throws MySQLException {
        DatabaseManager databaseManager = Context.getDatabaseManager();
        DatabaseInfo select = databaseManager.select(databaseInfo.getDatabaseName());
        if (select == null) {
            String message = String.format("数据库%s不存在", databaseInfo.getDatabaseName());
            throw new ExecuteException(message);
        }
        try (Stream<Path> walk = Files.walk(select.getDatabaseDir().toPath())) {
            walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            databaseManager.refresh();
            log.info("删除{}数据库", databaseInfo.getDatabaseName());
            return Result.success();
        } catch (IOException e) {
            throw new ExecuteException("删除数据库失败");
        }
    }
}
