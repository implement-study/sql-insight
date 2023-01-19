package org.gongxuanzhang.mysql.core.manager;

import org.gongxuanzhang.mysql.entity.DatabaseInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.tool.CollectionUtils;
import org.gongxuanzhang.mysql.tool.Context;

import java.io.File;

/**
 * 数据库管理
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class DatabaseManager extends AbstractManager<DatabaseInfo> {

    public DatabaseManager() throws MySQLException {
    }

    @Override
    protected String errorMessage() {
        return "数据库";
    }

    @Override
    protected void init() throws MySQLException {
        File home = Context.getHome();
        File[] databases = home.listFiles(File::isDirectory);
        CollectionUtils.foreachIfNotEmpty(databases, databaseFile -> {
            DatabaseInfo databaseInfo = new DatabaseInfo(databaseFile.getName());
            this.register(databaseInfo);
        });
    }

    @Override
    public String toId(DatabaseInfo databaseInfo) {
        return databaseInfo.getDatabaseName();
    }
}
