package org.gongxuanzhang.mysql.service.executor.session.show;

import org.gongxuanzhang.mysql.core.Result;
import org.gongxuanzhang.mysql.tool.ContextSupport;

import java.util.ArrayList;
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
        return Result.singleRow("database", new ArrayList<>(databases));
    }
}
