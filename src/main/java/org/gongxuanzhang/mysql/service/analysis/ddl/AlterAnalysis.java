package org.gongxuanzhang.mysql.service.analysis.ddl;

import org.gongxuanzhang.mysql.exception.SqlAnalysisException;
import org.gongxuanzhang.mysql.service.analysis.TokenAnalysis;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.TokenKind;
import org.gongxuanzhang.mysql.service.token.TokenSupport;
import org.gongxuanzhang.mysql.tool.ExceptionThrower;

import java.util.List;

/**
 * alter 解析器
 * truncate table(todo)
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class AlterAnalysis implements TokenAnalysis {


    @Override
    public Executor analysis(List<SqlToken> sqlTokenList) throws SqlAnalysisException {
        throw new UnsupportedOperationException("alter table 还没实现呐！");
    }



}
