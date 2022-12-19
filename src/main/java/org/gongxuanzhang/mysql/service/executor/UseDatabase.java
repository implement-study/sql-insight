package org.gongxuanzhang.mysql.service.executor;

import org.gongxuanzhang.mysql.core.MySqlSession;
import org.gongxuanzhang.mysql.core.Result;
import org.gongxuanzhang.mysql.core.SessionManager;

/**
 * 切换数据库
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class UseDatabase implements Executor {

    private final String database;

    public UseDatabase(String database) {
        this.database = database;
    }

    @Override
    public Result doExecute() {
        MySqlSession mySqlSession = SessionManager.currentSession();
        mySqlSession.useDatabase(database);
        return Result.success();
    }
}
