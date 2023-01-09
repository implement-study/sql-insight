package org.gongxuanzhang.mysql.service.analysis.ddl;

import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.SqlAnalysisException;
import org.gongxuanzhang.mysql.service.analysis.TokenAnalysis;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.executor.ddl.drop.DropTableExecutor;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.TokenSupport;
import org.gongxuanzhang.mysql.tool.ExceptionThrower;

import java.util.List;

/**
 * drop 解析器
 * drop table
 * create database
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class DropAnalysis implements TokenAnalysis {


    @Override
    public Executor analysis(List<SqlToken> sqlTokenList) throws SqlAnalysisException {
        final int offset = 1;
        SqlToken sqlToken = sqlTokenList.get(offset);
        switch (sqlToken.getTokenKind()) {
            case TABLE:
                return dropTable(sqlTokenList);
            case DATABASE:
                return dropDataBase(sqlTokenList);
            case PROCEDURE:
            case FUNCTION:
            case VIEW:
                throw new SqlAnalysisException("drop " + sqlToken.getTokenKind() + "还没有实现");
            default:
                throw new SqlAnalysisException("[create " + sqlToken.getValue() + "]有问题");
        }
    }

    private Executor dropDataBase(List<SqlToken> sqlTokenList) throws SqlAnalysisException {
        //todo
        return null;
    }

    private Executor dropTable(List<SqlToken> sqlTokenList) throws SqlAnalysisException {
        TableInfo tableInfo = new TableInfo();
        int i = TokenSupport.fillTableName(tableInfo, sqlTokenList, 2);
        ExceptionThrower.ifNotThrow(sqlTokenList.size() == i + 2);
        return new DropTableExecutor(tableInfo);
    }


}
