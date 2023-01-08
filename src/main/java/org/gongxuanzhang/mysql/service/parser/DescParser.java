package org.gongxuanzhang.mysql.service.parser;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.mysql.annotation.SQLParser;
import org.gongxuanzhang.mysql.exception.SqlParseException;
import org.gongxuanzhang.mysql.service.executor.DescTableExecutor;
import org.gongxuanzhang.mysql.service.executor.Executor;

/**
 * desc 的解析器
 * 查看表结构
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
@SQLParser
@Deprecated
public class DescParser implements DDLSqlParser {

    private static final String PREFIX = "desc";


    @Override
    public Executor parse(String[] split, String sql) throws SqlParseException {
        if (split.length != 2) {
            throw new SqlParseException("语法错误，无法解析");
        }
        String action = split[1];
        return new DescTableExecutor(null);
    }


    @Override
    public String prefix() {
        return PREFIX;
    }
}
