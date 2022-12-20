package org.gongxuanzhang.mysql.service.executor.show;

import org.gongxuanzhang.mysql.core.MySqlSession;
import org.gongxuanzhang.mysql.core.Result;
import org.gongxuanzhang.mysql.core.SessionManager;
import org.gongxuanzhang.mysql.entity.GlobalProperties;
import org.gongxuanzhang.mysql.exception.SqlParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * show variables
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class VariablesShower implements Shower {

    private final static String[] HEAD = new String[]{"variable_name", "value"};

    final int type;

    public VariablesShower(String sql) throws SqlParseException {
        if (sql.startsWith("show session")) {
            type = 1;
        } else if (sql.startsWith("show global")) {
            type = 2;
        } else if (sql.startsWith("show variables")) {
            type = 3;
        } else {
            throw new SqlParseException(sql + "无法解析");
        }
    }

    @Override
    public Result show() {
        switch (type) {
            case 1:
                return sessionShow();
            case 2:
                return globalShow();
            case 3:
                return defaultShow();
            default:
                throw new IllegalStateException("状态错误");
        }
    }

    private Result defaultShow() {
        try{
            MySqlSession mySqlSession = SessionManager.currentSession();
            GlobalProperties instance = GlobalProperties.getInstance();
            Map<String,String> attr = new HashMap<>(mySqlSession.getAllAttr());
            instance.getAllAttr().forEach(attr::putIfAbsent);
            return returnVar(attr);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    private Result globalShow() {
        try {
            GlobalProperties instance = GlobalProperties.getInstance();
            return returnVar(instance.getAllAttr());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    private Result sessionShow() {
        try {
            MySqlSession mySqlSession = SessionManager.currentSession();
            return returnVar(mySqlSession.getAllAttr());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    private Result returnVar(Map<String, String> allAttr) {
        List<Map<String, String>> data = new ArrayList<>();
        allAttr.forEach((k, v) -> {
            Map<String, String> map = new HashMap<>(4);
            map.put(HEAD[0], k);
            map.put(HEAD[1], v);
            data.add(map);
        });
        return Result.select(HEAD, data);
    }
}
