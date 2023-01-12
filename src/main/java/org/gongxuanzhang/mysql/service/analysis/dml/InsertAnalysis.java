package org.gongxuanzhang.mysql.service.analysis.dml;

import org.gongxuanzhang.mysql.entity.InsertInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.exception.SqlAnalysisException;
import org.gongxuanzhang.mysql.service.analysis.TokenAnalysis;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.TokenKind;
import org.gongxuanzhang.mysql.service.token.TokenSupport;

import java.util.List;

/**
 * insert 解析器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class InsertAnalysis implements TokenAnalysis {


    @Override
    public Executor analysis(List<SqlToken> sqlTokenList) throws MySQLException {
        TokenSupport.mustTokenKind(sqlTokenList.get(1), TokenKind.INTO);
        InsertInfo info = new InsertInfo();
        TokenSupport.fillTableName(info,sqlTokenList,1);

        throw new UnsupportedOperationException("insert  还没实现呐！");
    }


}
