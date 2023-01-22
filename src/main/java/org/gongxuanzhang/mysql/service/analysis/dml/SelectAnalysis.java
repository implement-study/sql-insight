package org.gongxuanzhang.mysql.service.analysis.dml;

import org.gongxuanzhang.mysql.core.select.As;
import org.gongxuanzhang.mysql.entity.SingleSelectInfo;
import org.gongxuanzhang.mysql.exception.SqlAnalysisException;
import org.gongxuanzhang.mysql.service.analysis.TokenAnalysis;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.TokenKind;
import org.gongxuanzhang.mysql.service.token.TokenSupport;

import java.util.List;

/**
 * select 解析器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SelectAnalysis implements TokenAnalysis {


    @Override
    public Executor analysis(List<SqlToken> sqlTokenList) throws SqlAnalysisException {
        SingleSelectInfo singleSelectInfo = new SingleSelectInfo();
        int offset = 1;
        As as;
        if(TokenSupport.isTokenKind(sqlTokenList.get(offset),TokenKind.PLUS)){
            as = null;
        }
        while (TokenSupport.isTokenKind(sqlTokenList.get(offset), TokenKind.FROM)) {
            //  todo fill as
        }
        return null;
    }


}
