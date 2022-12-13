package org.gongxuanzhang.mysql.entity;

import lombok.Data;

/**
 * 数据库信息
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class DatabaseInfo implements ExecuteInfo{

    private String databaseName;



}
