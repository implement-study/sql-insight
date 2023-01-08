package org.gongxuanzhang.mysql.service.executor.session.show;

import org.gongxuanzhang.mysql.core.MySqlSession;
import org.gongxuanzhang.mysql.core.Result;
import org.gongxuanzhang.mysql.core.SessionManager;
import org.gongxuanzhang.mysql.entity.GlobalProperties;
import org.gongxuanzhang.mysql.entity.ShowVarInfo;
import org.gongxuanzhang.mysql.entity.VariableInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.gongxuanzhang.mysql.tool.ExceptionThrower.errorSwap;

/**
 * show variables
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class VariablesShower implements Shower {

    private final static String[] HEAD = new String[]{"variable_name", "value"};

    private final ShowVarInfo info;

    public VariablesShower(ShowVarInfo info) {
        this.info = info;
    }

    @Override
    public Result show() throws MySQLException {
        if(info.isGlobal()){
            return globalShow();
        }
        if(info.isSession()){
            return sessionShow();
        }
        return defaultShow();
    }

    private Result defaultShow() throws MySQLException {
        try {
            MySqlSession mySqlSession = SessionManager.currentSession();
            GlobalProperties instance = GlobalProperties.getInstance();
            Map<String, String> attr = new HashMap<>(mySqlSession.getAllAttr());
            instance.getAllAttr().forEach(attr::putIfAbsent);
            return returnVar(attr);
        } catch (Exception e) {
            return errorSwap(e);
        }
    }

    private Result globalShow() throws MySQLException {
        try {
            GlobalProperties instance = GlobalProperties.getInstance();
            return returnVar(instance.getAllAttr());
        } catch (Exception e) {
            return errorSwap(e);
        }
    }

    private Result sessionShow() throws MySQLException {
        try {
            MySqlSession mySqlSession = SessionManager.currentSession();
            return returnVar(mySqlSession.getAllAttr());
        } catch (Exception e) {
            return errorSwap(e);
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
