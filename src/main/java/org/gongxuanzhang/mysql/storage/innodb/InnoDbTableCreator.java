package org.gongxuanzhang.mysql.storage.innodb;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.storage.CreateTableEngine;

/**
 * innodb 引擎的 create table
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
public class InnoDbTableCreator implements CreateTableEngine {

    @Override
    public Result createTable(TableInfo tableInfo) throws MySQLException {
        return null;
    }

}
