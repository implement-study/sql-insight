package org.gongxuanzhang.mysql.exception;

/**
 * mysql 异常
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class MySQLException extends Exception {

    public MySQLException() {
    }

    public MySQLException(String message) {
        super(message);
    }

    public MySQLException(String message, Throwable cause) {
        super(message, cause);
    }

    public MySQLException(Throwable cause) {
        super(cause);
    }

    public MySQLException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
