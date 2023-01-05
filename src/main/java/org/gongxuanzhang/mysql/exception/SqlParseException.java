package org.gongxuanzhang.mysql.exception;

/**
 * Sql 词法分析错误
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SqlParseException extends MySQLException {

    public SqlParseException(String message) {
        super(message);
    }
}
