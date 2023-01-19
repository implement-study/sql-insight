package org.gongxuanzhang.mysql.entity;


/**
 * 一个单元格的数据
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface Cell<T> extends ExecuteInfo {

    /**
     * 数据类型
     *
     * @return 不能为空
     **/
    ColumnType getType();

    /**
     * 返回具体值
     *
     * @return 可以为空
     **/
    T getValue();

}
