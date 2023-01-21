package org.gongxuanzhang.mysql.tool;

import org.gongxuanzhang.mysql.exception.MySQLException;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@FunctionalInterface
public interface LineHandle {

    /**
     * 行处理
     *
     * @param line 一行
     **/
    void handle(String line) throws MySQLException;

}
