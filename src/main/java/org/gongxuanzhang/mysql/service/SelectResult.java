package org.gongxuanzhang.mysql.service;

import lombok.Data;

import java.util.List;

/**
 * 查询结果返回实体
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class SelectResult implements Result {

    private final int code;

    private final String errorMessage;

    private List<MetaData> data;

}
