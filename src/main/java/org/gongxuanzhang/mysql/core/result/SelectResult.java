package org.gongxuanzhang.mysql.core.result;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 查询结果返回实体
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class SelectResult implements Result {

    private final String[] head;

    private final List<JSONObject> data;

    private String sqlTime;

    private String sql;

    @Override
    public int getCode() {
        return 200;
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
    public String getSql() {
        return sql;
    }

    @Override
    public void setSql(String sql) {
        this.sql = sql;
    }
}
