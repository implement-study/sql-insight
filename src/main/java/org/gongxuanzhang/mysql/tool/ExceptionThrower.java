package org.gongxuanzhang.mysql.tool;

import org.gongxuanzhang.mysql.exception.SqlAnalysisException;
import org.gongxuanzhang.mysql.exception.SqlParseException;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.TokenKind;

/**
 * 异常工具类
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class ExceptionThrower {

    private ExceptionThrower() {

    }

    /**
     * 词法解析错误
     **/
    public static void throwSqlAnalysis(String var) throws SqlAnalysisException {
        String message = "词法解析中[%s] 解析过程出现错误";
        throw new SqlAnalysisException(String.format(message, var));
    }

    /**
     * 语法解析错误
     **/
    public static void throwSqlParse(String var) throws SqlParseException {
        String message = "语法解析中[%s] 解析过程出现错误";
        throw new SqlParseException(String.format(message, var));
    }


    public static void errorNext(SqlToken sqlToken) throws SqlAnalysisException {
        throw new SqlAnalysisException(sqlToken.getValue() + "解析异常");
    }

    public static void expectToken(SqlToken sqlToken, TokenKind expect) throws SqlAnalysisException {
        if (sqlToken.getTokenKind() != expect) {
            throw new SqlAnalysisException(sqlToken.getValue() + "解析异常");
        }
    }
}
