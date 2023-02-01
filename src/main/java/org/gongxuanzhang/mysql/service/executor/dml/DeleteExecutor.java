package org.gongxuanzhang.mysql.service.executor.dml;

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.DeleteInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.executor.EngineExecutor;
import org.gongxuanzhang.mysql.storage.StorageEngine;

/**
 * 删除执行器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class DeleteExecutor extends EngineExecutor<DeleteInfo> {

    public DeleteExecutor(StorageEngine engine, DeleteInfo info) {
        super(engine, info);
    }

    @Override
    public Result doEngine(StorageEngine engine, DeleteInfo info) throws MySQLException {
        return engine.delete(info);
    }
}
