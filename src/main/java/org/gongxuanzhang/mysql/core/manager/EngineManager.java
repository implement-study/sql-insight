package org.gongxuanzhang.mysql.core.manager;

import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.storage.StorageEngine;

/**
 * 引擎管理
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class EngineManager extends AbstractManager<StorageEngine> {


    public EngineManager() throws MySQLException {
    }

    @Override
    protected void init() throws MySQLException {

    }

    @Override
    protected String errorMessage() {
        return "引擎";
    }

    @Override
    public String toId(StorageEngine engine) {
        return engine.getEngineName();
    }
}
