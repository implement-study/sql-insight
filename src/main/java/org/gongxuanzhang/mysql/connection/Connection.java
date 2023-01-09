package org.gongxuanzhang.mysql.connection;

import org.gongxuanzhang.mysql.core.MySqlEngine;
import org.gongxuanzhang.mysql.core.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模拟连接 只为了传递sql
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@RestController
public class Connection {

    @Autowired
    MySqlEngine mySqlEngine;


    @PostMapping("/execute")
    public Result execute(String sql) {
        return mySqlEngine.doSql(sql);
    }

//    @GetMapping("/openSession")
//    public Result openSession(){
//        MySqlSession mySqlSession = SessionManager.currentSession();
//    }

}
