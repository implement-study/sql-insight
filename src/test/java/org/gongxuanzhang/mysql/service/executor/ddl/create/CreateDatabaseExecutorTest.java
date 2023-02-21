package org.gongxuanzhang.mysql.service.executor.ddl.create;

import org.gongxuanzhang.mysql.connection.Connection;
import org.gongxuanzhang.mysql.tool.Context;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;


@SpringBootTest
class CreateDatabaseExecutorTest {


    private final String testDatabaseName = "test1";

    @Test
    public void createDatabase(@Autowired Connection connection) {
        String sql = "create database " + testDatabaseName;
        connection.execute(sql);
        File home = Context.getHome();
        File[] targetDir = home.listFiles((file) -> file.isDirectory() && file.getName().equals(testDatabaseName));
        Assertions.assertSame(targetDir.length, 1);
        Assertions.assertTrue(targetDir[0].exists());
    }

    @AfterEach
    public void deleteFile() {
        File home = Context.getHome();
        new File(home, testDatabaseName).delete();
    }


}
