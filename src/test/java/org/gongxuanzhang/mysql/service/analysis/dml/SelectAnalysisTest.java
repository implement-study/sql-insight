package org.gongxuanzhang.mysql.service.analysis.dml;

import org.gongxuanzhang.mysql.connection.Connection;
import org.gongxuanzhang.mysql.core.result.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class SelectAnalysisTest {


    @Test
    public void testAnalysisSingleAs(@Autowired Connection connection) {
        String sql = "select a as b,s as d from aa.user";
        connection.execute(sql);
    }

    @Test
    public void testAnalysisPlusAs(@Autowired Connection connection) {
        String sql = "select a as b,s as d ,* ,b from aa.user";
        connection.execute(sql);
    }

    @Test
    public void testAnalysisWhere(@Autowired Connection connection) {
        String sql = "select * from aa.user where 1=1 and id>1";
        Result execute = connection.execute(sql);
    }


}
