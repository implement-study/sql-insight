package org.gongxuanzhang.mysql.service.analysis.ast;

import org.gongxuanzhang.mysql.exception.SqlAnalysisException;
import org.gongxuanzhang.mysql.service.analysis.CreateAnalysis;
import org.gongxuanzhang.mysql.service.analysis.TokenAnalysis;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.TokenKind;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析一个子sql
 * 子sql不包括括号等内容
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Component
public class SubSqlAnalysis implements TokenAnalysis {

    private Map<TokenKind, TokenAnalysis> analysisMap = new HashMap<>();

    @PostConstruct
    public void init(){
        analysisMap.put(TokenKind.CREATE,new CreateAnalysis());
    }

    @Override
    public Executor analysis(List<SqlToken> sqlTokenList) throws SqlAnalysisException {
        if (CollectionUtils.isEmpty(sqlTokenList)) {
            throw new SqlAnalysisException("sql解析失败");
        }
        SqlToken sqlToken = sqlTokenList.get(0);
        TokenAnalysis tokenAnalysis = analysisMap.get(sqlToken.getTokenKind());
        if (tokenAnalysis == null) {
            throw new SqlAnalysisException("[" + sqlToken.getValue() + "]无法解析");
        }
        return tokenAnalysis.analysis(sqlTokenList);
    }


}
