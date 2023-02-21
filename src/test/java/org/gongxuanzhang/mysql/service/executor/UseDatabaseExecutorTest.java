package org.gongxuanzhang.mysql.service.executor;

import org.gongxuanzhang.mysql.connection.Connection;
import org.gongxuanzhang.mysql.core.MySqlSession;
import org.gongxuanzhang.mysql.core.SessionManager;
import org.gongxuanzhang.mysql.exception.SessionException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest()
class UseDatabaseExecutorTest {

    @Test
    public void testUseDatabase(@Autowired Connection connection) throws SessionException {

        MySqlSession mySqlSession = SessionManager.currentSession();
//        mySqlSession.getDatabase()


    }



}
