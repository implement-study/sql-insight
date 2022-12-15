package org.gongxuanzhang.mysql.entity;

import lombok.Data;

import java.util.List;

/**
 * 表信息
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class TableInfo implements ExecuteInfo {

    private String database;
    private String tableName;
    private List<ColumnInfo> columnInfos;
    private List<String> primaryKey;
    private String comment;
    private String engine;


}
