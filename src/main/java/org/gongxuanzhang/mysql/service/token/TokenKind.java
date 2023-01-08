package org.gongxuanzhang.mysql.service.token;

import org.gongxuanzhang.mysql.service.analysis.ast.SqlAstNode;

/**
 * token种类
 * 关键字归为一类
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public enum TokenKind implements SqlAstNode {


    /**
     * 标准字符串
     **/
    LITERACY,


    //  keyword
    TABLE,
    TABLES,
    DATABASE,
    DATABASES,


    //  Session
    SHOW,
    SET,
    USE,
    GLOBAL,
    SESSION,
    VARIABLES,

    //  DDL
    CREATE,
    ALTER,
    DROP,
    TRUNCATE,
    RENAME,
    DESC,
    DESCRIBE,

    //  DML
    SELECT,
    UPDATE,
    DELETE,
    INSERT,
    INTO,
    FROM,
    WHERE,
    JOIN,
    ON,
    OR,
    AND,


    //  data type
    INT,
    VARCHAR,
    TIMESTAMP,

    //  table special
    DEFAULT,
    AUTO_INCREMENT,
    NOT,
    NULL,
    UNIQUE,
    COMMENT,
    PRIMARY,
    KEY,


    /**
     * 一个变量
     **/
    VAR(false),

    LEFT_PAREN(false),
    RIGHT_PAREN(false),
    GT(false),
    GTE(false),
    LT(false),
    LTE(false),
    PLUS(false),
    MINUS(false),
    MULTI(false),
    COMMA(false),
    DOT(false),
    DIVIDE(false),
    NE(false),
    EQUALS(false),
    MOL(false),
    AT(false),
    DOUBLE_AT(false);

    private final boolean swap;

    TokenKind() {
        this(true);
    }

    TokenKind(boolean swap) {
        this.swap = swap;
    }

    public boolean canSwap() {
        return swap;
    }
}
