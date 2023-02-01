package org.gongxuanzhang.mysql.exception;

/**
 * sql语法分析错误
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SqlAnalysisException extends MySQLException {

    public SqlAnalysisException(Throwable cause) {
        super(String.format("sql解析出现问题,错误信息[%s]", cause.getMessage()),cause);
    }

    public SqlAnalysisException(String message) {
        super(message);
    }
}
