package org.gongxuanzhang.mysql.storage.fool;

import org.gongxuanzhang.mysql.annotation.Engine;
import org.gongxuanzhang.mysql.storage.StorageEngine;

/**
 * 傻子引擎，只有功能完全没有性能。
 * 为了搭建最基础的内容
 * 同时也展示了如果不优化，查询效率是有多低!
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Engine
public class FoolStorageEngine implements StorageEngine {


    @Override
    public String getEngineName() {
        return "fool";
    }

    @Override
    public boolean supportTransaction() {
        return false;
    }

}
