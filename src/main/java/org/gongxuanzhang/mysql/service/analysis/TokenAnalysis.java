package org.gongxuanzhang.mysql.service.analysis;

import org.gongxuanzhang.mysql.exception.SqlAnalysisException;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.token.SqlToken;

import java.util.List;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface TokenAnalysis {



    /**
     * 解析成一个执行器
     * @param sqlTokenList tokens
     * @return node
     **/
    Executor analysis(List<SqlToken> sqlTokenList) throws SqlAnalysisException;
}
