package org.gongxuanzhang.mysql.core;

/**
 * 提供组件是否可用的接口
 */
public interface Available {


    /**
     * 返回组件是否可用
     *
     * @return true可用 false不可用
     */
    boolean available();
}
