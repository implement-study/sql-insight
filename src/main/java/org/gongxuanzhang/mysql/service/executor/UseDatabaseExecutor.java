package org.gongxuanzhang.mysql.service.executor;

import org.gongxuanzhang.mysql.core.MySqlSession;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.core.SessionManager;
import org.gongxuanzhang.mysql.core.manager.DatabaseManager;
import org.gongxuanzhang.mysql.entity.DatabaseInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.tool.Context;

/**
 * 切换数据库
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class UseDatabaseExecutor implements Executor {

    private final String database;

    public UseDatabaseExecutor(String database) {
        this.database = database;
    }

    @Override
    public Result doExecute() throws MySQLException {
        MySqlSession mySqlSession = SessionManager.currentSession();
        DatabaseManager databaseManager = Context.getDatabaseManager();
        DatabaseInfo select = databaseManager.select(this.database);
        if (select == null) {
            return Result.error("数据库" + database + "不存在");
        }
        mySqlSession.useDatabase(database);
        return Result.success();
    }
}
