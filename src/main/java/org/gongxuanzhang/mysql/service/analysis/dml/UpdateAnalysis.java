package org.gongxuanzhang.mysql.service.analysis.dml;

import org.gongxuanzhang.mysql.exception.SqlAnalysisException;
import org.gongxuanzhang.mysql.service.analysis.TokenAnalysis;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.token.SqlToken;

import java.util.List;

/**
 * update 解析器
 *
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class UpdateAnalysis implements TokenAnalysis {


    @Override
    public Executor analysis(List<SqlToken> sqlTokenList) throws SqlAnalysisException {
        throw new UnsupportedOperationException("update 还没实现呐！");
    }



}
