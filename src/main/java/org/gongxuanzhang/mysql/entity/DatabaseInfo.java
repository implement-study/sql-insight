package org.gongxuanzhang.mysql.entity;

import lombok.Data;

import java.io.File;


/**
 * 数据库信息
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class DatabaseInfo implements ExecuteInfo {

    private String databaseName;

    /**
     * 数据库文件夹
     **/
    private File databaseDir;






}
