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
    VIEW,
    FUNCTION,
    PROCEDURE,


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
    VALUES,
    AS,


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
    ENGINES,


    /**
     * 一个变量
     **/
    VAR("${}"),

    LEFT_PAREN("("),
    RIGHT_PAREN(")"),
    GT(">"),
    GTE(">="),
    LT("<"),
    LTE("<="),
    PLUS("+"),
    MINUS("-"),
    MULTI("*"),
    COMMA(","),
    DOT("."),
    DIVIDE("/"),
    NE("!="),
    EQUALS("="),
    MOL("%"),
    AT("@"),
    DOUBLE_AT("@@");

    private final String symbol;

    TokenKind() {
        this("");
    }

    TokenKind(String symbol) {
        this.symbol = symbol;
    }

    public boolean canSwap() {
        return this.symbol.isEmpty();
    }
}
