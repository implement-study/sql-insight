package org.gongxuanzhang.mysql.service.parser;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.mysql.annotation.SQLParser;
import org.gongxuanzhang.mysql.exception.SqlParseException;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.executor.session.show.DatabaseShower;
import org.gongxuanzhang.mysql.service.executor.session.show.TablesShower;
import org.gongxuanzhang.mysql.service.executor.session.show.VariablesShower;

/**
 * show 的解析器
 * 支持各种意义上的show
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
@SQLParser
@Deprecated
public class ShowParser implements DDLSqlParser {

    private static final String PREFIX = "show";


    @Override
    public Executor parse(String[] split, String sql) throws SqlParseException {
        if (split.length < 2) {
            throw new SqlParseException("语法错误，无法解析");
        }
        String action = split[1];
        switch (action.toLowerCase()) {
            case "databases":
                return new DatabaseShower();
            case "tables":
                return new TablesShower();
            case "global":
            case "session":
            case "variables":
                return new VariablesShower(null);
            default:
                throw new SqlParseException(action + "不支持，你可以自定义功能来实现DDL");
        }
    }


    @Override
    public String prefix() {
        return PREFIX;
    }
}
