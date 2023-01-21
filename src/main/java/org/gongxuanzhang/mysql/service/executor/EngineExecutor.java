package org.gongxuanzhang.mysql.service.executor;

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.ExecuteInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.storage.StorageEngine;

/**
 * 和存储引擎相关的执行器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public abstract class EngineExecutor<T extends ExecuteInfo> implements Executor {

    private final StorageEngine engine;
    private final T info;

    public EngineExecutor(StorageEngine engine, T info) {
        this.engine = engine;
        this.info = info;
    }


    /**
     * 引擎执行
     *
     * @param info   执行信息
     * @param engine 执行引擎
     * @return 统一返回值
     * @throws MySQLException 执行过程出现问题
     **/
    public abstract Result doEngine(StorageEngine engine, T info) throws MySQLException;


    @Override
    public Result doExecute() throws MySQLException {
        return this.doEngine(engine, info);
    }


    public StorageEngine getEngine() {
        return engine;
    }

    public T getInfo() {
        return info;
    }
}
