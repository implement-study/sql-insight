package org.gongxuanzhang.mysql.service.executor.dml;

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.SingleSelectInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.executor.EngineExecutor;
import org.gongxuanzhang.mysql.storage.StorageEngine;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SelectExecutor extends EngineExecutor<SingleSelectInfo> {

    public SelectExecutor(StorageEngine engine, SingleSelectInfo info) {
        super(engine, info);
    }

    @Override
    public Result doEngine(StorageEngine engine, SingleSelectInfo info) throws MySQLException {
        return engine.select(info);
    }
}
