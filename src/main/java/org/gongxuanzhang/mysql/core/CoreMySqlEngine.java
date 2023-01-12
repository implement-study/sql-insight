package org.gongxuanzhang.mysql.core;

import org.gongxuanzhang.mysql.service.analysis.TokenAnalysis;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.SqlTokenizer;
import org.gongxuanzhang.mysql.tool.SqlUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Component
public class CoreMySqlEngine implements MySqlEngine {

    private final TokenAnalysis tokenAnalysis;

    public CoreMySqlEngine(TokenAnalysis tokenAnalysis) {
        this.tokenAnalysis = tokenAnalysis;
    }

    @Override
    public Result doSql(String sql) {
        try {
            long startTime = System.currentTimeMillis();
            SessionManager.currentSession().setSql(sql);
            SqlTokenizer tokenizer = new SqlTokenizer(sql);
            List<SqlToken> process = tokenizer.process();
            Executor executor = tokenAnalysis.analysis(process);
            Result result = executor.doExecute();
            result.setSqlTime(SqlUtils.sqlTime(startTime));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }

    }
}
