package org.gongxuanzhang.mysql.service.executor;

import org.gongxuanzhang.mysql.core.Result;
import org.gongxuanzhang.mysql.entity.VariableInfo;
import org.gongxuanzhang.mysql.entity.VariableUpdateInfo;
import org.gongxuanzhang.mysql.exception.SqlParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * 设置变量的执行器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SetExecutor extends AbstractInfoExecutor<VariableUpdateInfo> {


    public SetExecutor(String sql) throws SqlParseException {
        super(sql);
    }

    @Override
    public VariableUpdateInfo analysisInfo(String sql) throws SqlParseException {
        List<VariableInfo> infoList = new ArrayList<>();
        boolean global = sql.startsWith("set global");
        sql = sql.substring("set ".length());
        String[] split = sql.split(",");
        for (String varStr : split) {
            VariableInfo variableInfo = varStrToInfo(varStr);
            if (global) {
                variableInfo.setGlobal(true);
            }
            infoList.add(variableInfo);
        }
        VariableUpdateInfo result = new VariableUpdateInfo();
        result.setVariableInfos(infoList);
        return result;
    }

    private VariableInfo varStrToInfo(String varStr) throws SqlParseException {
        VariableInfo variableInfo = new VariableInfo();
        String[] split = varStr.split("=");
        if (split.length != 2) {
            throw new SqlParseException(varStr + "无法解析");
        }
        String varName = split[0];
        varName = varName.trim();
        boolean global = false;
        if (varName.startsWith("global ")) {
            global = true;
            varName = varName.substring("global ".length());
        }
        if(varName.startsWith("global.")){
            global = true;
            varName = varName.substring("global.".length());
        }

        String[] nameCombination = varName.trim().split(" ");
        if (nameCombination.length > 2) {
            throw new SqlParseException(varStr + "无法解析");
        }
        if (nameCombination.length == 2) {
        }


        variableInfo.setValue(split[1].trim());
        return variableInfo;
    }


    @Override
    public Result doExecute() {

        return Result.success();
    }
}
