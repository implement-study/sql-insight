package org.gongxuanzhang.mysql.service.analysis.ast;

import org.gongxuanzhang.mysql.exception.SqlAnalysisException;
import org.gongxuanzhang.mysql.service.analysis.TokenAnalysis;
import org.gongxuanzhang.mysql.service.analysis.ddl.AlterAnalysis;
import org.gongxuanzhang.mysql.service.analysis.ddl.CreateAnalysis;
import org.gongxuanzhang.mysql.service.analysis.ddl.DropAnalysis;
import org.gongxuanzhang.mysql.service.analysis.ddl.TruncateAnalysis;
import org.gongxuanzhang.mysql.service.analysis.dml.DeleteAnalysis;
import org.gongxuanzhang.mysql.service.analysis.dml.InsertAnalysis;
import org.gongxuanzhang.mysql.service.analysis.dml.SelectAnalysis;
import org.gongxuanzhang.mysql.service.analysis.dml.UpdateAnalysis;
import org.gongxuanzhang.mysql.service.analysis.session.DescAnalysis;
import org.gongxuanzhang.mysql.service.analysis.session.SetAnalysis;
import org.gongxuanzhang.mysql.service.analysis.session.ShowAnalysis;
import org.gongxuanzhang.mysql.service.analysis.session.UseAnalysis;
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

    private final Map<TokenKind, TokenAnalysis> analysisMap = new HashMap<>();

    @PostConstruct
    public void init() {
        analysisMap.put(TokenKind.CREATE, new CreateAnalysis());
        analysisMap.put(TokenKind.SET, new SetAnalysis());
        analysisMap.put(TokenKind.USE, new UseAnalysis());
        analysisMap.put(TokenKind.DESC, new DescAnalysis());
        analysisMap.put(TokenKind.DESCRIBE, analysisMap.get(TokenKind.DESC));
        analysisMap.put(TokenKind.SHOW, new ShowAnalysis());
        analysisMap.put(TokenKind.DROP, new DropAnalysis());
        analysisMap.put(TokenKind.TRUNCATE, new TruncateAnalysis());
        analysisMap.put(TokenKind.ALTER, new AlterAnalysis());
        analysisMap.put(TokenKind.SELECT, new SelectAnalysis());
        analysisMap.put(TokenKind.UPDATE, new UpdateAnalysis());
        analysisMap.put(TokenKind.INSERT, new InsertAnalysis());
        analysisMap.put(TokenKind.DELETE, new DeleteAnalysis());
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
        try {
            return tokenAnalysis.analysis(sqlTokenList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SqlAnalysisException(e);
        }

    }


}
