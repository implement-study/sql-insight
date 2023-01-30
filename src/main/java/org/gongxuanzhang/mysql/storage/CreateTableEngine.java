package org.gongxuanzhang.mysql.storage;

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;

/**
 * 建表引擎
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface CreateTableEngine {


    /**
     * 建表
     *
     * @param info 表信息
     * @return 通用返回值
     * @throws MySQLException 执行过程中出现问题抛出异常
     **/
    Result createTable(TableInfo info) throws MySQLException;
}
