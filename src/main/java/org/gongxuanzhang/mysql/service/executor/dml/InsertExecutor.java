package org.gongxuanzhang.mysql.service.executor.dml;

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.InsertInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.executor.EngineExecutor;
import org.gongxuanzhang.mysql.storage.StorageEngine;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class InsertExecutor extends EngineExecutor<InsertInfo> {

    public InsertExecutor(StorageEngine engine, InsertInfo info) {
        super(engine, info);
    }

    @Override
    public Result doEngine(StorageEngine engine, InsertInfo info) throws MySQLException {
        return engine.insert(info);
    }
}
