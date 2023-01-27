package org.gongxuanzhang.mysql.core.select;


import lombok.Data;

/**
 * 查询列内容
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class SelectCol {
    /**
     * 原列名
     **/
    private final String colName;
    /**
     * 列别名
     **/
    private final String alias;
    /**
     * 从'*'查询
     **/
    private final boolean all;

    private SelectCol(String colName, String alias, boolean all) {
        this.colName = colName;
        this.alias = alias;
        this.all = all;
    }

    public static SelectCol allCol() {
        return new SelectCol(null, null, true);
    }

    public static SelectCol single(String colName, String alias) {
        return new SelectCol(colName, alias, false);
    }

}
