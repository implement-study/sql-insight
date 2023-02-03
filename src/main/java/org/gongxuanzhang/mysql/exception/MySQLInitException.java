package org.gongxuanzhang.mysql.exception;

/**
 * mysql初始化异常
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class MySQLInitException extends RuntimeException {

    public MySQLInitException() {
    }

    public MySQLInitException(String message) {
        super(message);
    }

    public MySQLInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public MySQLInitException(Throwable cause) {
        super(cause);
    }

    public MySQLInitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
