package org.gongxuanzhang.mysql.core;

import org.gongxuanzhang.mysql.core.select.Order;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface OrderBox {

    /**
     * 设置order
     *
     * @param order order实体
     **/
    void setOrder(Order<?> order);


    /**
     * 返回order
     *
     * @return order实体
     **/
    Order<?> getOrder();
}
