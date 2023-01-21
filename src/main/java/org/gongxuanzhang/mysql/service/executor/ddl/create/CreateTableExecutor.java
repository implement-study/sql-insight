package org.gongxuanzhang.mysql.service.executor.ddl.create;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.executor.EngineExecutor;
import org.gongxuanzhang.mysql.storage.StorageEngine;

/**
 * 创建表
 * create table
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
public class CreateTableExecutor extends EngineExecutor<TableInfo> {


    public CreateTableExecutor(StorageEngine engine, TableInfo info) {
        super(engine, info);
    }

    @Override
    public Result doEngine(StorageEngine engine, TableInfo tableInfo) throws MySQLException {
        return engine.createTable(tableInfo);
    }

}
