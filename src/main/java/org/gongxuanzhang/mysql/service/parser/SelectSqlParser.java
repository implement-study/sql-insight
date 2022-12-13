package org.gongxuanzhang.mysql.service.parser;

import org.gongxuanzhang.mysql.exception.SqlParseException;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.executor.SelectExecutor;

/**
 * 解析查询sql
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SelectSqlParser implements SmartSqlParser {

    private static final String PREFIX = "select";

    @Override
    public Executor parse(String sql) throws SqlParseException {
        String[] split = sql.split("\\s+");
        return new SelectExecutor();
    }


    @Override
    public boolean support(String sql) {
        return sql.toLowerCase().startsWith(PREFIX);
    }
}
