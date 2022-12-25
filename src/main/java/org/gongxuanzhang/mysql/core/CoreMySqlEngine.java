package org.gongxuanzhang.mysql.core;

import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.parser.SqlParser;
import org.gongxuanzhang.mysql.tool.SqlUtils;
import org.springframework.stereotype.Component;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Component
public class CoreMySqlEngine implements MySqlEngine {

    private final SqlParser smartSqlParser;

    public CoreMySqlEngine(SqlParser smartSqlParser) {
        this.smartSqlParser = smartSqlParser;
    }

    @Override
    public Result doSql(String sql) {
        try {
            long startTime = System.currentTimeMillis();
            Executor executor = smartSqlParser.parse(sql);
            Result result = executor.doExecute();
            result.setSqlTime(SqlUtils.sqlTime(startTime));
            return result;
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }

    }
}
