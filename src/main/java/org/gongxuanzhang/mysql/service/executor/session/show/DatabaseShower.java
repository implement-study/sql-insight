package org.gongxuanzhang.mysql.service.executor.session.show;

import org.gongxuanzhang.mysql.core.manager.DatabaseManager;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.DatabaseInfo;
import org.gongxuanzhang.mysql.tool.Context;

import java.util.List;
import java.util.stream.Collectors;

/**
 * show databases
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class DatabaseShower implements Shower {


    @Override
    public Result show() {
        DatabaseManager databaseManager = Context.getDatabaseManager();
        List<DatabaseInfo> all = databaseManager.getAll();
        List<String> databases = all.stream().map(DatabaseInfo::getDatabaseName).collect(Collectors.toList());
        return Result.singleRow("database", databases);
    }
}
