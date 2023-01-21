package org.gongxuanzhang.mysql.storage.fool;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.mysql.annotation.Engine;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.DeleteInfo;
import org.gongxuanzhang.mysql.entity.InsertInfo;
import org.gongxuanzhang.mysql.entity.SelectInfo;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.entity.UpdateInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.storage.CreateTableEngine;
import org.gongxuanzhang.mysql.storage.DeleteEngine;
import org.gongxuanzhang.mysql.storage.InsertEngine;
import org.gongxuanzhang.mysql.storage.SelectEngine;
import org.gongxuanzhang.mysql.storage.StorageEngine;
import org.gongxuanzhang.mysql.storage.UpdateEngine;

/**
 * 傻子引擎，只有功能完全没有性能。
 * 为了搭建最基础的内容
 * 同时也展示了如果不优化，查询效率是有多低!
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Engine
@Slf4j
public class FoolStorageEngine implements StorageEngine {


    private final CreateTableEngine foolCreateTable = new FoolTableCreator();

    private final InsertEngine foolInsert = new FoolInsert();

    private final SelectEngine foolSelect = new FoolSelect();

    private final UpdateEngine foolUpdate = new FoolUpdate();

    private final DeleteEngine foolDelete = new FoolDelete();

    @Override
    public String getEngineName() {
        return "fool";
    }

    @Override
    public boolean supportTransaction() {
        return false;
    }

    @Override
    public Result createTable(TableInfo tableInfo) throws MySQLException {
        return foolCreateTable.createTable(tableInfo);
    }


    /**
     * 如果有唯一键
     * 要遍历全表
     * 如果没有 直接插入
     **/
    @Override
    public Result insert(InsertInfo info) throws MySQLException {
        return foolInsert.insert(info);
    }


    @Override
    public Result delete(DeleteInfo info) throws MySQLException {
        return foolDelete.delete(info);
    }

    @Override
    public Result update(UpdateInfo info) throws MySQLException {
        return foolUpdate.update(info);
    }

    @Override
    public Result select(SelectInfo info) throws MySQLException {
        return foolSelect.select(info);
    }


}
