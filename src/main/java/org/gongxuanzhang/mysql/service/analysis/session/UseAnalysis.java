package org.gongxuanzhang.mysql.service.analysis.session;

import org.gongxuanzhang.mysql.exception.SqlAnalysisException;
import org.gongxuanzhang.mysql.service.analysis.TokenAnalysis;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.executor.UseDatabaseExecutor;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.TokenSupport;
import org.gongxuanzhang.mysql.tool.ExceptionThrower;

import java.util.List;

/**
 * use 解析器
 * use databaseName
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class UseAnalysis implements TokenAnalysis {


    @Override
    public Executor analysis(List<SqlToken> sqlTokenList) throws SqlAnalysisException {
        ExceptionThrower.ifNotThrow(sqlTokenList.size() == 2, "sql 无法解析");
        String databaseName = TokenSupport.getMustVar(sqlTokenList.get(1));
        return new UseDatabaseExecutor(databaseName);
    }

}
