package org.gongxuanzhang.mysql.service.executor.dml;

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.DeleteInfo;
import org.gongxuanzhang.mysql.entity.UpdateInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.executor.EngineExecutor;
import org.gongxuanzhang.mysql.storage.StorageEngine;

/**
 * update 执行器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class UpdateExecutor extends EngineExecutor<UpdateInfo> {

    public UpdateExecutor(StorageEngine engine, UpdateInfo info) {
        super(engine, info);
    }

    @Override
    public Result doEngine(StorageEngine engine, UpdateInfo info) throws MySQLException {
        return engine.update(info);
    }
}
