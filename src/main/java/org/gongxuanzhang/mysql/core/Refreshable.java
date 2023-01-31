package org.gongxuanzhang.mysql.core;

import org.gongxuanzhang.mysql.exception.MySQLException;

/**
 *
 * 可刷新组件
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface Refreshable {


    /**
     *
     * 更新
     **/
    void refresh() throws MySQLException;
}
