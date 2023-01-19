package org.gongxuanzhang.mysql.service.analysis;

import org.gongxuanzhang.mysql.exception.ExecuteException;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.exception.SqlAnalysisException;
import org.gongxuanzhang.mysql.exception.SqlParseException;
import org.gongxuanzhang.mysql.service.analysis.ast.SubSqlAnalysis;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.SqlTokenizer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
class CreateAnalysisTest {

    @Test
    void analysis(@Autowired SubSqlAnalysis subSqlAnalysis) throws MySQLException {
        String sql = "create table aa.user( id int primary key, name varchar)";
        SqlTokenizer sqlTokenizer = new SqlTokenizer(sql);
        List<SqlToken> process = sqlTokenizer.process();
        Executor analysis = subSqlAnalysis.analysis(process);
        analysis.doExecute();

    }

    @Test
    void analysis1(@Autowired SubSqlAnalysis subSqlAnalysis) throws MySQLException {
        String sql = "create table bbb.user(" +
                "id int primary key auto_increment," +
                "name varchar comment '名字' ) comment='zhangsan'";
        SqlTokenizer sqlTokenizer = new SqlTokenizer(sql);
        List<SqlToken> process = sqlTokenizer.process();
        Executor analysis = subSqlAnalysis.analysis(process);
        analysis.doExecute();

    }
}
