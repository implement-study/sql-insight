package org.gongxuanzhang.mysql.core;

/**
 * 错误返回
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class ErrorResult implements Result {


    private final String errorMessage;

    ErrorResult(String errorMessage) {
        if (errorMessage == null) {
            throw new NullPointerException("错误信息不能为null");
        }
        this.errorMessage = errorMessage;
    }

    @Override
    public int getCode() {
        return 500;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String getSqlTime() {
        return null;
    }

    @Override
    public void setSqlTime(String sqlTime) {

    }
}
