package org.gongxuanzhang.mysql.service.analysis.dml;

import org.gongxuanzhang.mysql.core.select.As;
import org.gongxuanzhang.mysql.core.select.Where;
import org.gongxuanzhang.mysql.entity.SingleSelectInfo;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.exception.SqlAnalysisException;
import org.gongxuanzhang.mysql.service.analysis.TokenAnalysis;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.TokenKind;
import org.gongxuanzhang.mysql.service.token.TokenSupport;
import org.gongxuanzhang.mysql.tool.Pair;

import java.util.List;

/**
 * select 解析器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SelectAnalysis implements TokenAnalysis {


    @Override
    public Executor analysis(List<SqlToken> sqlTokenList) throws MySQLException {
        SingleSelectInfo singleSelectInfo = new SingleSelectInfo();
        Pair<Integer,As> asPair = analysisAlias(sqlTokenList,1);
        Pair<Integer,TableInfo> tablePair = analysisTable(sqlTokenList,1+asPair.getKey());
        Pair<Integer,Where> wherePair = analysisWhere(sqlTokenList,1+tablePair.getKey());
        singleSelectInfo.setAs(asPair.getValue());
        singleSelectInfo.setMainTable(tablePair.getValue());
        singleSelectInfo.setWhere(wherePair.getValue());
        return null;
    }

    /**
     * 解析别名
     */
    private Pair<Integer,As> analysisAlias(List<SqlToken> sqlTokenList, int start) throws MySQLException {
        int offset = 0;
        As as = new As();
        //  todo
        while(!TokenSupport.isTokenKind(sqlTokenList.get(start+offset),TokenKind.FROM)){
            String colName = TokenSupport.getMustVar(sqlTokenList.get(start + offset));
            offset++;
            if (TokenSupport.isTokenKind(sqlTokenList.get(start+offset), TokenKind.AS)) {
                offset++;
            }
            String alias = TokenSupport.getString(sqlTokenList.get(start + offset));

        }
        return null;
    }

    /**
     * 解析table
     */
    private Pair<Integer, TableInfo> analysisTable(List<SqlToken> sqlTokenList, int start) throws MySQLException{
        return null;
    }

    /**
     * 解析where
     */
    private Pair<Integer, Where> analysisWhere(List<SqlToken> sqlTokenList, int offset) throws MySQLException{

        return null;
    }





}
