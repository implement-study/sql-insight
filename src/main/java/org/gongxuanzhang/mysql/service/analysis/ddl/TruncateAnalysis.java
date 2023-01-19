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
 * truncate 解析器
 * truncate table(todo)
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class TruncateAnalysis implements TokenAnalysis {


    @Override
    public Executor analysis(List<SqlToken> sqlTokenList) throws SqlAnalysisException {
        ExceptionThrower.ifNotThrow(sqlTokenList.size() == 3);
        TokenSupport.mustTokenKind(sqlTokenList.get(1), TokenKind.TABLE);
        String tableName = TokenSupport.getMustVar(sqlTokenList.get(1));
        throw new UnsupportedOperationException("truncate table 还没实现呐！");
    }


}
