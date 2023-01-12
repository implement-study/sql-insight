package org.gongxuanzhang.mysql.service.executor.session.show;

import org.gongxuanzhang.mysql.core.MySqlSession;
import org.gongxuanzhang.mysql.core.Result;
import org.gongxuanzhang.mysql.core.SessionManager;
import org.gongxuanzhang.mysql.tool.CollectionUtils;
import org.gongxuanzhang.mysql.tool.Context;

import java.io.File;
import java.util.List;


/**
 * show tables
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class TablesShower implements Shower {


    @Override
    public Result show() {
        try {
            MySqlSession session = SessionManager.currentSession();
            String database = session.getDatabase();
            File databaseHome = Context.getDatabaseHome(database);
            File[] tableFiles = databaseHome.listFiles((f) -> f.getName().endsWith(".gfrm"));
            final String key = "tables_in_" + database;
            List<String> tableNames = CollectionUtils.arrayToList(tableFiles, this::fileToTableName);
            return Result.singleRow(key, tableNames);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    private String fileToTableName(File gfrm) {
        return gfrm.getName().split("\\.")[0];
    }
}
