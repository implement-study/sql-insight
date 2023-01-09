package org.gongxuanzhang.mysql.service.analysis.dml;

import org.gongxuanzhang.mysql.exception.SqlAnalysisException;
import org.gongxuanzhang.mysql.service.analysis.TokenAnalysis;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.TokenKind;
import org.gongxuanzhang.mysql.service.token.TokenSupport;
import org.gongxuanzhang.mysql.tool.ExceptionThrower;

import java.util.List;

/**
 * select 解析器
 *
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SelectAnalysis implements TokenAnalysis {


    @Override
    public Executor analysis(List<SqlToken> sqlTokenList) throws SqlAnalysisException {
        throw new UnsupportedOperationException("select  还没实现呐！");
    }



}
