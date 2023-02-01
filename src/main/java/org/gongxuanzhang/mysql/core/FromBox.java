package org.gongxuanzhang.mysql.core;

import org.gongxuanzhang.mysql.core.select.From;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface FromBox {

    /**
     * 设置from
     *
     * @param from from实体
     **/
    void setFrom(From from);


    /**
     * 返回from
     *
     * @return from实体
     **/
    From getFrom();
}
