package org.gongxuanzhang.mysql.storage.innodb;

import org.gongxuanzhang.mysql.annotation.Engine;
import org.gongxuanzhang.mysql.storage.StorageEngine;

/**
 * InnoDB引擎实现
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Engine
public class InnoDb implements StorageEngine {


    @Override
    public String getEngineName() {
        return "innoDb";
    }

    @Override
    public boolean supportTransaction() {
        return true;
    }
}
