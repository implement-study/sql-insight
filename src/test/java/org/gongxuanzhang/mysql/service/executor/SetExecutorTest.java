package org.gongxuanzhang.mysql.service.executor;

import org.gongxuanzhang.mysql.entity.VariableInfo;
import org.gongxuanzhang.mysql.entity.VariableUpdateInfo;
import org.gongxuanzhang.mysql.exception.SqlParseException;
import org.gongxuanzhang.mysql.tool.SqlUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;



class SetExecutorTest {


    @Test
    public void oneVar() throws SqlParseException {
        String sql = "set global aa=100";
        sql = SqlUtils.formatSql(sql);
        VariableInfo info = new SetExecutor(sql).getInfo();
        VariableInfo target = new VariableInfo();
        target.setGlobal(true);
        target.setName("aa");
        target.setValue("100");
        Assertions.assertEquals(info, target);
    }


}
