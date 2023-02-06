package org.gongxuanzhang.mysql.service.analysis.dml;

import org.gongxuanzhang.mysql.connection.Connection;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.analysis.ast.SubSqlAnalysis;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.SqlTokenizer;
import org.gongxuanzhang.mysql.tool.Console;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


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


    @Test
    public void testAnalysisOrderSingle(@Autowired SubSqlAnalysis subSqlAnalysis) throws MySQLException{
        String sql = "select * from aa.user order by id";
        SqlTokenizer sqlTokenizer = new SqlTokenizer(sql);
        List<SqlToken> process = sqlTokenizer.process();
        Executor analysis = subSqlAnalysis.analysis(process);
    }


    @Test
    public void testAnalysisOrderTargetOrder(@Autowired SubSqlAnalysis subSqlAnalysis) throws MySQLException {
        String sql = "select * from aa.user order by id desc";
        SqlTokenizer sqlTokenizer = new SqlTokenizer(sql);
        List<SqlToken> process = sqlTokenizer.process();
        Executor analysis = subSqlAnalysis.analysis(process);


    }

    @Test
    public void testAnalysisMultiOrder(@Autowired Connection connection) {
        String sql = "select * from aa.user order by id name";
    }

    @Test
    public void testAnalysisOrderComplex(@Autowired Connection connection) {
        String sql = "select * from aa.user order by id desc, name asc,age";
    }

    @Test
    public void testAnalysisOrder(@Autowired Connection connection) {
        String sql = "select * from aa.user order by id desc";
        Console.infoResult(connection.execute(sql));
    }


}
