package org.gongxuanzhang.mysql.service.analysis.dml;

import org.gongxuanzhang.mysql.core.select.As;
import org.gongxuanzhang.mysql.core.select.SelectCol;
import org.gongxuanzhang.mysql.core.select.Where;
import org.gongxuanzhang.mysql.entity.SingleSelectInfo;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
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
        int offset = 1;
        while (!TokenSupport.isTokenKind(sqlTokenList.get(offset), TokenKind.FROM)) {
            offset++;
        }
        As as = analysisAlias(sqlTokenList.subList(1, offset));
//        Pair<Integer,TableInfo> tablePair = analysisTable(sqlTokenList,1+asPair.getKey());
//        Pair<Integer,Where> wherePair = analysisWhere(sqlTokenList,1+tablePair.getKey());
//        singleSelectInfo.setAs(asPair.getValue());
//        singleSelectInfo.setMainTable(tablePair.getValue());
//        singleSelectInfo.setWhere(wherePair.getValue());
        return null;
    }

    /**
     * 解析别名
     */
    private As analysisAlias(List<SqlToken> sqlTokenList) throws MySQLException {
        if (sqlTokenList.isEmpty()) {
            throw new MySQLException("无法解析列");
        }
        int offset = 0;
        As as = new As();
        while (offset < sqlTokenList.size()) {
            //  *
            if (TokenSupport.isTokenKind(sqlTokenList.get(offset), TokenKind.MULTI)) {
                offset += multiAs(as, sqlTokenList, offset);
            } else {
                offset += singleAs(as, sqlTokenList, offset);
            }
        }
        return as;
    }


    /**
     * 解析带*号的as
     *
     * @return 返回偏移量
     **/
    private int multiAs(As as, List<SqlToken> sqlTokenList, int start) throws MySQLException {
        int offset = 0;
        TokenSupport.mustTokenKind(sqlTokenList.get(offset + start), TokenKind.MULTI);
        offset++;
        if (offset + start < sqlTokenList.size() &&
                TokenSupport.isTokenKind(sqlTokenList.get(offset + start), TokenKind.COMMA)) {
            offset++;
        }
        as.addSelectCol(SelectCol.allCol());
        return offset;
    }


    /**
     * 解析单列
     *
     * @return 返回偏移量
     **/
    private int singleAs(As as, List<SqlToken> sqlTokenList, int start) throws MySQLException {
        int offset = 0;
        String key = TokenSupport.getString(sqlTokenList.get(start + offset));
        offset++;
        if (offset + start < sqlTokenList.size() &&
                TokenSupport.isTokenKind(sqlTokenList.get(start + offset), TokenKind.AS)) {
            offset++;
        }
        String value = null;
        if (offset + start < sqlTokenList.size() &&
                TokenSupport.isTokenKind(sqlTokenList.get(start + offset), TokenKind.LITERACY, TokenKind.VAR)) {
            value = TokenSupport.getString(sqlTokenList.get(start + offset));
            offset++;
        }
        as.addSelectCol(SelectCol.single(key, value));
        //  判断下一个是否是逗号或者结束
        if (offset + start < sqlTokenList.size() && TokenSupport.isTokenKind(sqlTokenList.get(offset),
                TokenKind.COMMA)) {
            offset++;
        }
        return offset;
    }


    /**
     * 解析table
     */
    private Pair<Integer, TableInfo> analysisTable(List<SqlToken> sqlTokenList, int start) throws MySQLException {
        return null;
    }

    /**
     * 解析where
     */
    private Pair<Integer, Where> analysisWhere(List<SqlToken> sqlTokenList, int offset) throws MySQLException {

        return null;
    }


}
