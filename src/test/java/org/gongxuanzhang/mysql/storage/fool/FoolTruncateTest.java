package org.gongxuanzhang.mysql.storage.fool;

import org.gongxuanzhang.mysql.connection.Connection;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.exception.SqlAnalysisException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class FoolTruncateTest {

    @Test
    public void truncate(@Autowired Connection connection){
        String sql = "truncate table aa.user";
        connection.execute(sql);
    }

    @Test
    public void truncateError(@Autowired Connection connection){
        String sql = "truncate table aa.user 1";
        System.out.println(connection.execute(sql));
    }

}
