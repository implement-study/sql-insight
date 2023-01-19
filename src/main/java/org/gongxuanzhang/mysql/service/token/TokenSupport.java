package org.gongxuanzhang.mysql.service.token;

import org.gongxuanzhang.mysql.core.SessionManager;
import org.gongxuanzhang.mysql.core.TableInfoBox;
import org.gongxuanzhang.mysql.entity.Cell;
import org.gongxuanzhang.mysql.entity.ColumnType;
import org.gongxuanzhang.mysql.entity.DatabaseInfo;
import org.gongxuanzhang.mysql.entity.IntCell;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.entity.TimeStampCell;
import org.gongxuanzhang.mysql.entity.VarcharCell;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.exception.SqlAnalysisException;
import org.gongxuanzhang.mysql.tool.ConvertUtils;
import org.gongxuanzhang.mysql.tool.ThrowableRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.gongxuanzhang.mysql.tool.ExceptionThrower.throwSqlAnalysis;

/**
 * token 辅助类
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class TokenSupport {

    private static final int FLAG_LENGTH = 'z' + 1;

    private static final byte[] FLAGS = new byte[FLAG_LENGTH];

    private static final byte DIGIT = 0x01;

    private static final byte ALPHABET = 0x02;

    private static final byte IDENTIFIER = 0x04;

    private static final Map<String, TokenKind> SWAP_TOKEN_KIND = new HashMap<>();


    static {
        for (int ch = '0'; ch <= '9'; ch++) {
            FLAGS[ch] |= DIGIT | IDENTIFIER;
        }
        for (int ch = 'A'; ch <= 'Z'; ch++) {
            FLAGS[ch] |= ALPHABET | IDENTIFIER;
        }
        for (int ch = 'a'; ch <= 'z'; ch++) {
            FLAGS[ch] |= ALPHABET | IDENTIFIER;
        }
        FLAGS['_'] |= IDENTIFIER;
        FLAGS['$'] |= IDENTIFIER;
        Arrays.stream(TokenKind.values()).filter(TokenKind::canSwap).forEach((kind) -> {
            SWAP_TOKEN_KIND.put(kind.toString(), kind);
        });
    }

    /**
     * 判断字符是否是数字
     *
     * @param c char
     * @return true是数字
     **/
    public static boolean isDigit(char c) {
        if (c >= FLAG_LENGTH) {
            return false;
        }
        return (FLAGS[c] & DIGIT) != 0;
    }


    /**
     * @param c char
     * @return true是字母
     **/
    public static boolean isAlphabet(char c) {
        if (c >= FLAG_LENGTH) {
            return false;
        }
        return (FLAGS[c] & ALPHABET) != 0;
    }


    /**
     * 是否是标识符
     *
     * @param c char
     * @return true是标识符
     **/
    public static boolean isIdentifier(char c) {
        if (c >= FLAG_LENGTH) {
            return false;
        }
        return (FLAGS[c] & IDENTIFIER) != 0;
    }


    /**
     * 尝试交换关键字
     *
     * @param sqlToken var sql token
     * @return 如果可以交换，返回交换之后的关键字 sql token 如果不能交换，返回原token
     **/
    public static SqlToken swapKeyword(SqlToken sqlToken) {
        if (sqlToken.getTokenKind() != TokenKind.VAR) {
            return sqlToken;
        }
        TokenKind tokenKind = SWAP_TOKEN_KIND.get(sqlToken.getValue().toUpperCase());
        if (tokenKind == null) {
            return sqlToken;
        }
        return new SqlToken(tokenKind, tokenKind.toString());
    }

    /**
     * 判断token类型是否是目标类型
     *
     * @param tokenKind token类型
     * @param target    目标类型
     * @return true是目标类型  false不是目标类型
     **/
    public static boolean isTokenKind(TokenKind tokenKind, TokenKind... target) {
        if (target == null || target.length == 0) {
            throw new NullPointerException("目标类型不能为空");
        }
        for (TokenKind kind : target) {
            if (kind == tokenKind) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTokenKind(SqlToken sqlToken, TokenKind... target) {
        return isTokenKind(sqlToken.getTokenKind(), target);
    }

    /**
     * 通过一个sql token var 拿到一个结果 要求token必须是var
     *
     * @return token的值
     * @throws SqlAnalysisException 如果不是 var token 抛出异常
     **/
    public static String getMustVar(SqlToken sqlToken) throws SqlAnalysisException {
        if (!isTokenKind(sqlToken, TokenKind.VAR)) {
            throw new SqlAnalysisException(sqlToken.getValue() + "解析错误");
        }
        return sqlToken.getValue();
    }

    /**
     * 通过一个sql token  拿到一个结果  要求token必须是LITERACY
     *
     * @param sqlToken sql token
     * @return token的值
     * @throws SqlAnalysisException 如果不是 LITERACY token 抛出异常
     **/
    public static String getMustLiteracy(SqlToken sqlToken) throws SqlAnalysisException {
        if (!isTokenKind(sqlToken, TokenKind.LITERACY)) {
            throw new SqlAnalysisException(sqlToken.getValue() + "解析错误");
        }
        return sqlToken.getValue();
    }

    public static void mustTokenKind(SqlToken sqlToken, TokenKind... tokenKind) throws SqlAnalysisException {
        if (!isTokenKind(sqlToken, tokenKind)) {
            throwSqlAnalysis(sqlToken.getValue());
        }
    }

    public static Cell<?> parseCell(SqlToken sqlToken) throws MySQLException {
        switch (sqlToken.getTokenKind()) {
            case VAR:
                return new VarcharCell(sqlToken.getValue());
            case INT:
                return new IntCell(ConvertUtils.convert(ColumnType.INT, sqlToken.getValue()));
            case TIMESTAMP:
                return new TimeStampCell(ConvertUtils.convert(ColumnType.TIMESTAMP, sqlToken.getValue()));
            default:
                throw new MySQLException(sqlToken.getTokenKind() + "无法解析");
        }
    }


    /**
     * 分析token  解析出 数据库和表名
     * 填充到表信息中
     *
     * @param tableInfo 表信息实体
     * @param tokenList token 流
     * @param offset    token流从哪开始解析
     * @return 返回使用了多少个流
     **/
    public static int fillTableName(TableInfo tableInfo, List<SqlToken> tokenList, int offset) throws MySQLException {
        String candidate = TokenSupport.getMustVar(tokenList.get(offset));
        if (tokenList.size() < offset + 3) {
            tableInfo.setTableName(candidate);
            String database = SessionManager.currentSession().getDatabase();
            tableInfo.setDatabase(new DatabaseInfo(database));
            return 1;
        }
        if (TokenSupport.isTokenKind(tokenList.get(offset + 1), TokenKind.DOT)) {
            String tableName = TokenSupport.getMustVar(tokenList.get(offset + 2));
            tableInfo.setDatabase(new DatabaseInfo(candidate));
            tableInfo.setTableName(tableName);
            return 3;
        }
        tableInfo.setTableName(candidate);
        return 1;
    }

    public static int fillTableName(TableInfo tableInfo, List<SqlToken> tokenList) throws MySQLException {
        return fillTableName(tableInfo, tokenList, 0);
    }

    public static int fillTableName(TableInfoBox box, List<SqlToken> tokenList, int offset) throws MySQLException {
        return fillTableName(box.getTableInfo(), tokenList, offset);
    }

    public static int fillTableName(TableInfoBox box, List<SqlToken> tokenList) throws MySQLException {
        return fillTableName(box.getTableInfo(), tokenList, 0);
    }


    public static TokenChain token(SqlToken token) {
        return new TokenChain(token);

    }

    /**
     * 辅助支持的链式调用的内部类
     **/
    public static class TokenChain {
        final SqlToken token;
        Map<TokenKind, ThrowableRunnable> actions;
        ThrowableRunnable elseAction;

        TokenChain(SqlToken token) {
            this.token = token;
            this.actions = new HashMap<>();
        }

        public When when(TokenKind tokenKind) {
            return new When(this, tokenKind);
        }

        public TokenChain elseRun(ThrowableRunnable elseAction) {
            this.elseAction = elseAction;
            return this;
        }

        public void get() throws MySQLException {
            ThrowableRunnable runnable = actions.get(token.getTokenKind());
            if (runnable != null) {
                runnable.run();
                return;
            }
            if (elseAction != null) {
                elseAction.run();
            }
        }

    }

    public static class When {
        final TokenChain chain;
        final TokenKind targetKind;

        public When(TokenChain chain, TokenKind targetKind) {
            this.chain = chain;
            this.targetKind = targetKind;
        }


        public TokenChain then(ThrowableRunnable runnable) {
            this.chain.actions.put(this.targetKind, runnable);
            return chain;
        }
    }
}
