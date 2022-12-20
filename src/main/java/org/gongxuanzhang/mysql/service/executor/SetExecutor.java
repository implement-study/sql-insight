package org.gongxuanzhang.mysql.service.executor;

import org.gongxuanzhang.mysql.core.MySqlSession;
import org.gongxuanzhang.mysql.core.Result;
import org.gongxuanzhang.mysql.core.SessionManager;
import org.gongxuanzhang.mysql.entity.GlobalProperties;
import org.gongxuanzhang.mysql.entity.VariableInfo;
import org.gongxuanzhang.mysql.exception.SqlParseException;
import org.gongxuanzhang.mysql.tool.SqlUtils;

/**
 * 设置变量的执行器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SetExecutor extends AbstractInfoExecutor<VariableInfo> {


    public SetExecutor(String sql) throws SqlParseException {
        super(sql);
    }

    @Override
    public VariableInfo analysisInfo(String sql) throws SqlParseException {
        String[] split = sql.split("=");
        if (split.length != 2) {
            throw new SqlParseException(sql + "无法解析");
        }
        VariableInfo info = new VariableInfo();
        String prefix = split[0];
        String value = split[1];
        info.setValue(value.trim());
        String[] prefixStr = prefix.trim().split(" ");
        String varName;
        switch (prefixStr.length) {
            case 2:
                varName = prefixStr[1];
                break;
            case 3:
                if (prefixStr[1].equals("global")) {
                    info.setGlobal(true);
                } else if (prefixStr[1].equals("session")) {
                    info.setGlobal(false);
                } else {
                    throw new SqlParseException(sql + "无法解析");
                }
                varName = prefixStr[2];
                break;
            default:
                throw new SqlParseException(sql + "无法解析");
        }
        SqlUtils.checkVarName(varName);
        info.setName(varName);
        return info;
    }


    @Override
    public Result doExecute() {
        VariableInfo info = getInfo();
        try {
            if (info.isGlobal()) {
                GlobalProperties.getInstance().set(info.getName(), info.getValue());
            } else {
                MySqlSession mySqlSession = SessionManager.currentSession();
                mySqlSession.set(info.getName(), info.getValue());
            }
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }
}
