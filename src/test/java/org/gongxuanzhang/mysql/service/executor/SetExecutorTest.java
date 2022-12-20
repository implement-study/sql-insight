package org.gongxuanzhang.mysql.service.executor;

import org.gongxuanzhang.mysql.entity.VariableInfo;
import org.gongxuanzhang.mysql.entity.VariableUpdateInfo;
import org.gongxuanzhang.mysql.exception.SqlParseException;
import org.gongxuanzhang.mysql.tool.SqlUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


class SetExecutorTest {


    @Test
    public void oneVar() throws SqlParseException {
        String sql = "set global aa=100";
        sql = SqlUtils.formatSql(sql);
        VariableUpdateInfo info = new SetExecutor(sql).getInfo();
        VariableUpdateInfo target = new VariableUpdateInfo();
        List<VariableInfo> infoList = new ArrayList<>();
        VariableInfo variableInfo = new VariableInfo();
        variableInfo.setName("aa");
        variableInfo.setValue("100");
        variableInfo.setGlobal(true);
        infoList.add(variableInfo);
        target.setVariableInfos(infoList);
        Assertions.assertEquals(info, target);
    }

    @Test
    public void multiVar() throws SqlParseException {
        String sql = "set global aa=100,bb=199";
        sql = SqlUtils.formatSql(sql);
        VariableUpdateInfo info = new SetExecutor(sql).getInfo();
        VariableUpdateInfo target = new VariableUpdateInfo();
        List<VariableInfo> infoList = new ArrayList<>();
        VariableInfo variableInfo1 = new VariableInfo();
        variableInfo1.setName("aa");
        variableInfo1.setValue("100");
        variableInfo1.setGlobal(true);
        infoList.add(variableInfo1);
        VariableInfo variableInfo2 = new VariableInfo();
        variableInfo2.setName("bb");
        variableInfo2.setValue("199");
        variableInfo2.setGlobal(true);
        infoList.add(variableInfo2);
        target.setVariableInfos(infoList);
        Assertions.assertEquals(info, target);
    }

}
