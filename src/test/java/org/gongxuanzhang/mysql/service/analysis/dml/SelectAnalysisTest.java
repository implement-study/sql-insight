package org.gongxuanzhang.mysql.service.analysis.dml;

import org.gongxuanzhang.mysql.connection.Connection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class SelectAnalysisTest {


    @Test
    public void testAnalysisSingleAs(@Autowired Connection connection){
        String sql = "select a as b,s as d from aa.user";
        connection.execute(sql);
    }

    @Test
    public void testAnalysisPlusAs(@Autowired Connection connection){
        String sql = "select a as b,s as d ,* ,b from aa.user";
        connection.execute(sql);
    }



}
