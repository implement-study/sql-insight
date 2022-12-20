package org.gongxuanzhang.mysql.service.executor.show;

import org.gongxuanzhang.mysql.core.Result;
import org.gongxuanzhang.mysql.tool.ContextSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * show databases
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class DatabaseShower implements Shower {


    @Override
    public Result show() {
        Set<String> databases = ContextSupport.getDatabases();

        if (databases.isEmpty()) {
            return Result.select(new String[]{"database"}, null);
        }
        List<Map<String, String>> data = new ArrayList<>();
        databases.forEach((db) -> {
            Map<String, String> item = new HashMap<>();
            item.put("database", db);
            data.add(item);
        });
        return Result.select(new String[]{"database"}, data);
    }
}
