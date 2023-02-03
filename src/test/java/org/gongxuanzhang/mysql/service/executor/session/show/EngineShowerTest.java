package org.gongxuanzhang.mysql.service.executor.session.show;

import org.gongxuanzhang.mysql.connection.Connection;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.tool.Console;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class EngineShowerTest {

    @Test
    public void showEngine(@Autowired Connection connection){
        String sql = "show engines";
        Result result = connection.execute(sql);
        Console.infoResult(result);
    }


}
