package org.gongxuanzhang.mysql.storage.innodb;

import org.gongxuanzhang.mysql.annotation.Engine;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.DeleteInfo;
import org.gongxuanzhang.mysql.entity.InsertInfo;
import org.gongxuanzhang.mysql.entity.SingleSelectInfo;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.entity.UpdateInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.storage.StorageEngine;

/**
 * InnoDB引擎实现
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Engine
public class InnoDb implements StorageEngine {



    @Override
    public Result createTable(TableInfo info) throws MySQLException {
        return null;
    }

    @Override
    public Result delete(DeleteInfo info) throws MySQLException {
        return null;
    }

    @Override
    public Result insert(InsertInfo info) throws MySQLException {
        return null;
    }

    @Override
    public Result select(SingleSelectInfo info) throws MySQLException {
        return null;
    }

    @Override
    public String getEngineName() {
        return null;
    }

    @Override
    public boolean supportTransaction() {
        return false;
    }

    @Override
    public Result update(UpdateInfo info) throws MySQLException {
        return null;
    }
}
