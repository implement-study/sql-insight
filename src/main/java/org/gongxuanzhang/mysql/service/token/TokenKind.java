package org.gongxuanzhang.mysql.service.token;

/**
 * token种类
 * 关键字归为一类
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public enum TokenKind {


    /**
     * 标准字符串
     **/
    LITERACY,

    /**
     * 一个变量
     **/
    VAR,

    LEFT_PAREN,

    RIGHT_PAREN


}
