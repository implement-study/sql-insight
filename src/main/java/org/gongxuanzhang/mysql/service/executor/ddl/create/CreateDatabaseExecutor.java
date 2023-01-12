package org.gongxuanzhang.mysql.service.executor.ddl.create;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.mysql.core.MySqlProperties;
import org.gongxuanzhang.mysql.core.Result;
import org.gongxuanzhang.mysql.entity.DatabaseInfo;
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


    private final DatabaseInfo databaseInfo;

    public CreateDatabaseExecutor(DatabaseInfo databaseInfo) {
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
        if (file.exists() || !file.mkdirs()) {
            throw new ExecuteException("数据库" + databaseName + "已经存在");
        }
        databaseInfo.setDatabaseDir(file);
        log.info("创建{}数据库", databaseName);
        Context.getDatabaseManager().register(databaseInfo);
        return Result.success();
    }
}
