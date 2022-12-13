package org.gongxuanzhang.mysql.connection;

import org.gongxuanzhang.mysql.service.Result;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.parser.SqlParser;
import org.gongxuanzhang.mysql.storage.innodb.InnoDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模拟连接 只为了传递sql
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@RestController
public class Connection {

    @Autowired
    private SqlParser smartSqlParser;

    @GetMapping("/execute")
    public Result execute(String sql) {
        sql = sql.trim();
        Executor parse = smartSqlParser.parse(sql);
        return parse.doExecute(new InnoDb());
    }
}
