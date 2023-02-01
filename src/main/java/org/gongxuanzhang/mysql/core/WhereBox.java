package org.gongxuanzhang.mysql.core;

import org.gongxuanzhang.mysql.core.select.Where;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface WhereBox {

    /**
     * 设置where
     *
     * @param where where实体
     **/
    void setWhere(Where where);


    /**
     * 返回where
     *
     * @return where实体
     **/
    Where getWhere();
}
