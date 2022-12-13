package org.gongxuanzhang.mysql.core;

/**
 * 表达式
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface Expression<V> {

    /**
     * 返回表达式的值
     *
     * @return value
     **/
    V getValue();
}
