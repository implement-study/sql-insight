package org.gongxuanzhang.mysql.core;

/**
 * 成功返回
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SuccessResult implements Result {


    SuccessResult() {

    }

    @Override
    public int getCode() {
        return 200;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }
}
