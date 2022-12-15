package org.gongxuanzhang.mysql.service.parser;

import org.gongxuanzhang.mysql.annotation.SQLParser;
import org.gongxuanzhang.mysql.exception.SqlParseException;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.executor.SelectExecutor;

/**
 * 解析查询sql
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@SQLParser
public class SelectParser implements DMLSqlParser {

    private static final String PREFIX = "select";

    @Override
    public Executor parse(String[] split, String sql) throws SqlParseException {
        return new SelectExecutor();
    }

    @Override
    public String prefix() {
        return PREFIX;
    }


}
