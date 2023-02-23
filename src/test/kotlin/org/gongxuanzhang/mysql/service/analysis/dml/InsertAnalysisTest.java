package org.gongxuanzhang.mysql.service.analysis.dml;

import org.gongxuanzhang.mysql.connection.Connection;
import org.gongxuanzhang.mysql.entity.Cell;
import org.gongxuanzhang.mysql.entity.InsertInfo;
import org.gongxuanzhang.mysql.entity.IntCell;
import org.gongxuanzhang.mysql.entity.VarcharCell;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.executor.dml.InsertExecutor;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.SqlTokenizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SpringBootTest
class InsertAnalysisTest {


    @Test
    public void haveCol() throws MySQLException {
        String sql = "insert into aa.aa(id,name) values(1,'s')";
        InsertAnalysis insertAnalysis = new InsertAnalysis();
        SqlTokenizer sqlTokenizer = new SqlTokenizer(sql);
        List<SqlToken> process = sqlTokenizer.process();
        InsertExecutor insertExecutor = (InsertExecutor) insertAnalysis.analysis(process);
        InsertInfo info = insertExecutor.getInfo();
        InsertInfo other = new InsertInfo();
        other.setColumns(Arrays.asList("id", "name"));
        List<Cell<?>> cells = Arrays.asList(new IntCell(1), new VarcharCell("s"));
        other.setInsertData(Collections.singletonList(cells));
        other.setTableInfo(info.getTableInfo());
        Assertions.assertEquals(info, other);

    }

    @Test
    public void multiCol() throws MySQLException {
        String sql = "insert into aa.aa(id,name) values(1,'s'),(2,'sadf')";
        InsertAnalysis insertAnalysis = new InsertAnalysis();
        SqlTokenizer sqlTokenizer = new SqlTokenizer(sql);
        List<SqlToken> process = sqlTokenizer.process();
        InsertExecutor insertExecutor = (InsertExecutor) insertAnalysis.analysis(process);
        InsertInfo info = insertExecutor.getInfo();
        InsertInfo other = new InsertInfo();
        other.setColumns(Arrays.asList("id", "name"));
        other.setInsertData(Arrays.asList(Arrays.asList(new IntCell(1), new VarcharCell("s")),
                Arrays.asList(new IntCell(2), new VarcharCell("sadf"))));
        other.setTableInfo(info.getTableInfo());
        Assertions.assertEquals(info, other);

    }

    @Test
    public void tableName() throws MySQLException {
        String sql = "insert into aa(id,name) values (1,'s')";
        InsertAnalysis insertAnalysis = new InsertAnalysis();
        SqlTokenizer sqlTokenizer = new SqlTokenizer(sql);
        List<SqlToken> process = sqlTokenizer.process();
        insertAnalysis.analysis(process);
    }

    @Test
    public void insertAutoIncrement(@Autowired Connection connection) throws MySQLException {
        String sql = "insert into aa.user (name) values('自增')";
        connection.execute(sql);
    }

}
