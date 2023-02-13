package org.gongxuanzhang.mysql.service.analysis.dml;

import org.gongxuanzhang.mysql.connection.Connection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class UpdateAnalysisTest {


    @Test
    public void updateAnalysis(@Autowired Connection connection){
        String sql = "update aa.user set a = 1 , b='a'";
        connection.execute(sql);

    }

}
