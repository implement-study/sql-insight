package org.gongxuanzhang.mysql.exception;

/**
 * 引擎执行异常
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class EngineException extends ExecuteException {

    public EngineException(String message) {
        super(message);
    }
}
