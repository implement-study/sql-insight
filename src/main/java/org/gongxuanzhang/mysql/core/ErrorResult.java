package org.gongxuanzhang.mysql.core;

import org.gongxuanzhang.mysql.core.result.Result;

/**
 * 错误返回
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class ErrorResult implements Result {


    private final String errorMessage;

    private String sql;

    public ErrorResult(String errorMessage, String sql) {
        if (errorMessage == null) {
            throw new NullPointerException("错误信息不能为null");
        }
        this.errorMessage = errorMessage;
        this.sql = sql;
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


    @Override
    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }
}
