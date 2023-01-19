package org.gongxuanzhang.mysql.service.analysis.dml;

import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.SqlTokenizer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class InsertAnalysisTest {


    @Test
    public void haveCol() throws MySQLException {
        String sql = "insert into aa.aa(id,name) values(1,'s')";
        InsertAnalysis insertAnalysis = new InsertAnalysis();
        SqlTokenizer sqlTokenizer = new SqlTokenizer(sql);
        List<SqlToken> process = sqlTokenizer.process();
        insertAnalysis.analysis(process);

    }

    @Test
    public void tableName() throws MySQLException {
        String sql = "insert into aa(id,name) values (1,'s')";
        InsertAnalysis insertAnalysis = new InsertAnalysis();
        SqlTokenizer sqlTokenizer = new SqlTokenizer(sql);
        List<SqlToken> process = sqlTokenizer.process();
        insertAnalysis.analysis(process);
    }

}
