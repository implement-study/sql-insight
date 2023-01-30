package org.gongxuanzhang.mysql;

import org.gongxuanzhang.mysql.connection.Connection;
import org.gongxuanzhang.mysql.core.result.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Init {


    @Test
    public void initDatabase(@Autowired Connection connection){
        String sql = "create database aa";
        connection.execute(sql);
    }

    @Test
    public void initTable(@Autowired Connection connection){
        String sql = "create table aa.user(id int primary key auto_increment,name varchar )";
        connection.execute(sql);
    }

    @Test
    public void insert(@Autowired Connection connection){
        String sql = "insert into aa.user(id,name) values(1,'张三'),(2,'李四')";
        connection.execute(sql);
    }

    @Test
    public void select(@Autowired Connection connection){
        String sql = "select * from aa.user";
        Result execute = connection.execute(sql);
    }

}
