package org.gongxuanzhang.mysql.exception;

/**
 * 执行异常
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class ExecuteException extends MySQLException {

    public ExecuteException(String message) {
        super(message);
    }
}
