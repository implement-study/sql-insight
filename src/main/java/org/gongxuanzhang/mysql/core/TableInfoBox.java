package org.gongxuanzhang.mysql.core;

import org.gongxuanzhang.mysql.entity.TableInfo;

/**
 * 包含表信息的信息
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface TableInfoBox {

    /**
     * 表信息
     *
     * @return 不能为null
     **/
    TableInfo getTableInfo();

    /**
     * 设置表信息
     *
     * @param tableInfo 不会为null
     **/
    void setTableInfo(TableInfo tableInfo);
}
