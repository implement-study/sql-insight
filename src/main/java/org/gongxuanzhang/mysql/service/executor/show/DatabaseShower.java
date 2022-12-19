package org.gongxuanzhang.mysql.service.executor.show;

import org.gongxuanzhang.mysql.core.Result;
import org.gongxuanzhang.mysql.tool.ContextSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * show databases
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class DatabaseShower implements Shower {


    @Override
    public Result show() {
        File home = ContextSupport.getHome();
        File[] dbs = home.listFiles(File::isDirectory);
        if (dbs == null) {
            return Result.select(new String[]{"database"},null);
        }
        List<Map<String, String>> data = new ArrayList<>();
        Arrays.stream(dbs).map(File::getName).forEach((db) -> {
            Map<String, String> item = new HashMap<>();
            item.put("database", db);
            data.add(item);
        });
        return Result.select(new String[]{"database"}, data);
    }
}
