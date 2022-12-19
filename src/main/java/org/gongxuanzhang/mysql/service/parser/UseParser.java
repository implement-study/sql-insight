package org.gongxuanzhang.mysql.service.parser;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.mysql.annotation.SQLParser;
import org.gongxuanzhang.mysql.exception.SqlParseException;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.executor.UseDatabase;

/**
 * create 的解析器
 * 支持创建表，创建数据库
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
@SQLParser
public class UseParser implements DDLSqlParser {

    private static final String PREFIX = "use";


    @Override
    public Executor parse(String[] split, String sql) throws SqlParseException {
        if (split.length != 2) {
            throw new SqlParseException("语法错误，无法解析");
        }
        String database = split[1];
        return new UseDatabase(database);
    }


    @Override
    public String prefix() {
        return PREFIX;
    }
}
