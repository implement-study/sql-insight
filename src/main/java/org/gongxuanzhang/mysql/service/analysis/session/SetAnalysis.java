package org.gongxuanzhang.mysql.service.analysis.session;

import org.gongxuanzhang.mysql.entity.VariableInfo;
import org.gongxuanzhang.mysql.exception.SqlAnalysisException;
import org.gongxuanzhang.mysql.service.analysis.TokenAnalysis;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.executor.session.SetExecutor;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.TokenKind;

import java.util.List;

import static org.gongxuanzhang.mysql.service.token.TokenSupport.getMustString;
import static org.gongxuanzhang.mysql.service.token.TokenSupport.isTokenKind;
import static org.gongxuanzhang.mysql.service.token.TokenSupport.mustTokenKind;
import static org.gongxuanzhang.mysql.service.token.TokenSupport.varString;

/**
 * set 解析器
 * '@@'表示设置系统变量
 * '@' 表示用户自定义变量
 * 如果没有'@' 表示系统变量
 * global表示全局变量
 * session表示会话变量
 * 默认为会话变量
 * <p>
 * 设置系统变量   如果加了session 或者global限定词 会被视为系统变量
 * set global sys = 'b'
 * set @@global.sys = 'b';
 * set session sys = 'b'
 * <p>
 * 设置自定义变量
 * set @custom = 'abc'
 * set @session.custom = 'abc';
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SetAnalysis implements TokenAnalysis {


    @Override
    public Executor analysis(List<SqlToken> sqlTokenList) throws SqlAnalysisException {
        VariableInfo info = new VariableInfo();

        SqlToken sqlToken = sqlTokenList.get(1);
        switch (sqlToken.getTokenKind()) {
            case GLOBAL:
                info.setGlobal(true);
                return determinerAnalysis(sqlTokenList, info);
            case SESSION:
                return determinerAnalysis(sqlTokenList, info);
            case AT:
            case DOUBLE_AT:
                return atAnalysis(sqlTokenList, info);
            case VAR:
                return kvAnalysis(sqlTokenList, 1, info);
            default:
                throw new SqlAnalysisException("[ " + sqlToken.getValue() + "]无法解析");
        }
    }

    /**
     * 用@符限定的解析方式
     * '@@'表示系统变量
     * <p>
     * set @session.a = 1;
     * set @a = 1;
     * set @@session.a = 1;
     * set @@global.a = 1
     **/
    private Executor atAnalysis(List<SqlToken> sqlTokenList, VariableInfo info) throws SqlAnalysisException {
        info.setSystem(isTokenKind(sqlTokenList.get(1), TokenKind.DOUBLE_AT));
        if (!isTokenKind(sqlTokenList.get(2), TokenKind.SESSION, TokenKind.GLOBAL)) {
            //  如果没有 global session限定词
            return kvAnalysis(sqlTokenList, 2, info);
        }
        info.setGlobal(isTokenKind(sqlTokenList.get(2), TokenKind.GLOBAL));
        mustTokenKind(sqlTokenList.get(3), TokenKind.DOT);
        return kvAnalysis(sqlTokenList, 4, info);
    }

    /**
     * 已经有了限定词的解析方式
     * 有限定词默认为系统变量 不允许使用 @修饰
     * set global a = 1
     * set session a = 1
     **/
    private Executor determinerAnalysis(List<SqlToken> sqlTokenList, VariableInfo info) throws SqlAnalysisException {
        info.setSystem(true);
        mustTokenKind(sqlTokenList.get(2), TokenKind.VAR);
        return kvAnalysis(sqlTokenList, 2, info);
    }

    /**
     * 最后解析赋值语句
     *
     * @return 返回个啥
     **/
    private Executor kvAnalysis(List<SqlToken> sqlTokenList, int offset, VariableInfo info) throws SqlAnalysisException {
        if (sqlTokenList.size() != offset + 3) {
            throw new SqlAnalysisException("sql解析失败");
        }
        String var = varString(sqlTokenList.get(offset));
        info.setName(var);
        mustTokenKind(sqlTokenList.get(offset + 1), TokenKind.EQUALS);
        String value = getMustString(sqlTokenList.get(offset + 2));
        info.setValue(value);
        return new SetExecutor(info);
    }

}
