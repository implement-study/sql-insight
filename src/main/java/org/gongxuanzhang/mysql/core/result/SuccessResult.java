package org.gongxuanzhang.mysql.core.result;

/**
 * 成功返回,没有任何信息
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SuccessResult implements Result {


    private String sqlTime;
    private String sql;


    @Override
    public int getCode() {
        return 100;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    @Override
    public String getSqlTime() {
        return sqlTime;
    }

    @Override
    public void setSqlTime(String sqlTime) {
        this.sqlTime = sqlTime;
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
