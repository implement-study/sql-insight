package org.gongxuanzhang.mysql.exception;

/**
 * sql语法分析错误
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SqlAnalysisException extends MySQLException {

    public SqlAnalysisException(String message) {
        super(message);
    }
}
