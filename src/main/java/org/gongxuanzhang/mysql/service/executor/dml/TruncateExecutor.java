package org.gongxuanzhang.mysql.service.executor.dml;

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.TruncateInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.executor.EngineExecutor;
import org.gongxuanzhang.mysql.storage.StorageEngine;

/**
 * truncate执行器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class TruncateExecutor extends EngineExecutor<TruncateInfo> {

    public TruncateExecutor(StorageEngine engine, TruncateInfo info) {
        super(engine, info);
    }

    @Override
    public Result doEngine(StorageEngine engine, TruncateInfo info) throws MySQLException {
        return engine.truncate(info);
    }
}
