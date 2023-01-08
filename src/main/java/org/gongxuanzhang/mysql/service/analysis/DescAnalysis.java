package org.gongxuanzhang.mysql.service.analysis;

import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.SqlAnalysisException;
import org.gongxuanzhang.mysql.service.executor.DescTableExecutor;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.TokenKind;
import org.gongxuanzhang.mysql.service.token.TokenSupport;
import org.gongxuanzhang.mysql.tool.ExceptionThrower;

import java.util.List;

/**
 * desc or describe 解析器
 * desc tableName
 * describe tableName
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class DescAnalysis implements TokenAnalysis {


    @Override
    public Executor analysis(List<SqlToken> sqlTokenList) throws SqlAnalysisException {
        String candidate = TokenSupport.varString(sqlTokenList.get(1));
        TableInfo tableInfo = new TableInfo();
        if (sqlTokenList.size() == 2) {
            tableInfo.setTableName(candidate);
            return new DescTableExecutor(tableInfo);
        }
        ExceptionThrower.ifNotThrow(sqlTokenList.size() == 4);
        TokenSupport.mustTokenKind(sqlTokenList.get(2), TokenKind.DOT);
        tableInfo.setDatabase(candidate);
        String tableName = TokenSupport.varString(sqlTokenList.get(3));
        tableInfo.setTableName(tableName);
        return new DescTableExecutor(tableInfo);
    }

}
