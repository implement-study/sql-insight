package org.gongxuanzhang.mysql.core;

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

    private final List<Map<String, String>> data;

    @Override
    public int getCode() {
        return 200;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }
}
