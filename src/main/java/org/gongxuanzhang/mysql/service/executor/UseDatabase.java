package org.gongxuanzhang.mysql.service.executor;

import org.gongxuanzhang.mysql.core.MySqlSession;
import org.gongxuanzhang.mysql.core.Result;
import org.gongxuanzhang.mysql.core.SessionManager;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.tool.ContextSupport;

import java.util.Set;

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
        try {
            MySqlSession mySqlSession = SessionManager.currentSession();
            Set<String> databases = ContextSupport.getDatabases();
            if (!databases.contains(database)) {
                return Result.error("数据库" + database + "不存在");
            }
            mySqlSession.useDatabase(database);
            return Result.success();
        } catch (MySQLException e) {
            return Result.error(e.getMessage());
        }
    }
}
