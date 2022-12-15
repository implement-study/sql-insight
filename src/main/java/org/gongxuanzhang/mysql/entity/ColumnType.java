package org.gongxuanzhang.mysql.entity;


/**
 * 列类型
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public enum ColumnType {

    /**
     * 数字
     **/
    INT("int"),
    /**
     * 字符串
     **/
    STRING("varchar"),
    /**
     * 时间戳
     **/
    TIMESTAMP("timestamp");


    public final String keyword;

    ColumnType(String keyword) {
        this.keyword = keyword;
    }
}
