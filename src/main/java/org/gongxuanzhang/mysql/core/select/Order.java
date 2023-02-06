package org.gongxuanzhang.mysql.core.select;

import org.gongxuanzhang.mysql.core.Available;

import java.util.Comparator;

/**
 * 排序字段
 * 用于辅助查询
 *
 * @author gongxuanzhang
 */
public interface Order<T> extends Comparator<T>, Available {


    /**
     * 添加排序列
     *
     * @param col       列名
     * @param orderEnum 排序方式
     **/
    void addOrder(String col, OrderEnum orderEnum);

    /**
     * 默认升序
     *
     * @param col 列名
     **/
    default void addOrder(String col) {
        addOrder(col, OrderEnum.asc);
    }
}
