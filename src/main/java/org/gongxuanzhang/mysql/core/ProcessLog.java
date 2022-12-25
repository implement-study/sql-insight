package org.gongxuanzhang.mysql.core;

import java.util.concurrent.DelayQueue;

/**
 * 记录执行的sql记录
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class ProcessLog {

    private final int capacity;

    public ProcessLog(int capacity) {
        this.capacity = capacity <= 0 ? Integer.MAX_VALUE : capacity;
    }

    public static void main(String[] args) {

    }
}
