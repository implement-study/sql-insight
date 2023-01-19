package org.gongxuanzhang.mysql.tool;

import org.gongxuanzhang.mysql.exception.MySQLException;

/**
 * 可抛异常的runnable
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@FunctionalInterface
public interface ThrowableRunnable {

    /**
     * 一个命令模式
     * 类似于 {@link Runnable}
     *
     * @throws MySQLException 可以抛出异常
     **/
    void run() throws MySQLException;
}
