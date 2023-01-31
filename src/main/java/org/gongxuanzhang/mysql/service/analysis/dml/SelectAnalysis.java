package org.gongxuanzhang.mysql.service.analysis.dml;

import org.gongxuanzhang.mysql.core.select.As;
import org.gongxuanzhang.mysql.core.select.Condition;
import org.gongxuanzhang.mysql.core.select.From;
import org.gongxuanzhang.mysql.core.select.SelectCol;
import org.gongxuanzhang.mysql.core.select.Where;
import org.gongxuanzhang.mysql.entity.SingleSelectInfo;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.analysis.TokenAnalysis;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.executor.dml.SelectExecutor;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.TokenKind;
import org.gongxuanzhang.mysql.service.token.TokenSupport;
import org.gongxuanzhang.mysql.storage.StorageEngine;
import org.gongxuanzhang.mysql.tool.Context;

import java.util.ArrayList;
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
        offset += fillAs(singleSelectInfo, sqlTokenList.subList(1, sqlTokenList.size()));
        offset += fillFrom(singleSelectInfo, sqlTokenList.subList(offset, sqlTokenList.size()));
        analysisWhere(singleSelectInfo, sqlTokenList.subList(offset, sqlTokenList.size()));
        StorageEngine engine = Context.selectStorageEngine(singleSelectInfo.getFrom().getMain());
        return new SelectExecutor(engine, singleSelectInfo);
    }


    /**
     * 解析from
     * todo 目前只支持单表解析
     **/
    private int fillFrom(SingleSelectInfo info, List<SqlToken> sqlTokenList) throws MySQLException {
        From from = new From();
        TokenSupport.mustTokenKind(sqlTokenList.get(0), TokenKind.FROM);
        info.setFrom(from);
        int tableOffset = TokenSupport.fillTableInfo(from, sqlTokenList, 1);
        //  from
        return tableOffset + 1;
    }

    /**
     * 解析别名
     */
    private int fillAs(SingleSelectInfo singleSelectInfo, List<SqlToken> sqlTokenList) throws MySQLException {
        int offset = 0;
        As as = new As();
        while (offset < sqlTokenList.size() && TokenSupport.isNotTokenKind(sqlTokenList.get(offset), TokenKind.FROM)) {
            //  *
            if (TokenSupport.isTokenKind(sqlTokenList.get(offset), TokenKind.MULTI)) {
                offset += multiAs(as, sqlTokenList, offset);
            } else {
                offset += singleAs(as, sqlTokenList, offset);
            }
        }
        if (as.isEmpty()) {
            throw new MySQLException("无法解析列");
        }
        singleSelectInfo.setAs(as);
        return offset;
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
     * 解析where
     */
    private int analysisWhere(SingleSelectInfo singleSelectInfo, List<SqlToken> sqlTokenList) throws MySQLException {
        Where where = new Where();
        singleSelectInfo.setWhere(where);
        if (sqlTokenList.isEmpty()) {
            return 0;
        }
        TokenSupport.mustTokenKind(sqlTokenList.get(0), TokenKind.WHERE);
        int offset = 1;
        boolean and = true;
        List<SqlToken> subTokenList = new ArrayList<>();
        while (offset < sqlTokenList.size()) {
            if (TokenSupport.isTokenKind(sqlTokenList.get(offset), TokenKind.AND)) {
                fillWhereCondition(subTokenList, where, and);
                and = true;
            } else if (TokenSupport.isTokenKind(sqlTokenList.get(offset), TokenKind.OR)) {
                fillWhereCondition(subTokenList, where, and);
                and = false;
            } else {
                subTokenList.add(sqlTokenList.get(offset));
            }
            offset++;
        }
        fillWhereCondition(subTokenList, where, and);
        return offset;
    }

    private void fillWhereCondition(List<SqlToken> subTokenList, Where where, boolean and) throws MySQLException {
        where.addCondition(and ? Condition.and(subTokenList) : Condition.or(subTokenList));
        subTokenList.clear();
    }


}
