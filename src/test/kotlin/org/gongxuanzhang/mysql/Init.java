package org.gongxuanzhang.mysql;

import org.gongxuanzhang.mysql.connection.Connection;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.tool.Console;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

@SpringBootTest
public class Init {


    @Test
    public void initDatabase(@Autowired Connection connection) {
        String sql = "create database aa";
        connection.execute(sql);
    }

    @Test
    public void initTable(@Autowired Connection connection) {
        String sql = "create table aa.user(id int primary key auto_increment,name varchar )";
        connection.execute(sql);
    }

    @Test
    public void insert(@Autowired Connection connection) {
        String sql = "insert into aa.user(id,name) values(1,'张三'),(2,'李四')";
        connection.execute(sql);
    }

    @Test
    public void insetRandomUser(@Autowired Connection connection) {
        String sql = "insert into aa.user(name) values";
        List<String> names = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String name = UUID.randomUUID().toString().replaceAll("-", "");
            names.add(String.format("('%s')", name));
        }
        StringJoiner stringJoiner = new StringJoiner(",");
        for (String name : names) {
            stringJoiner.add(name);
        }
        sql += stringJoiner.toString();
        connection.execute(sql);

    }

    @Test
    public void select(@Autowired Connection connection) {
        String sql = "select * from aa.user";
        Result execute = connection.execute(sql);
    }

    @Test
    public void selectOrder(@Autowired Connection connection) {
        String sql = "select * from aa.user order by id desc";
        Console.infoResult(connection.execute(sql));
    }

}
