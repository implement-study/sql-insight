package org.gongxuanzhang.mysql.storage;

import org.gongxuanzhang.mysql.annotation.DependOnContext;
import org.gongxuanzhang.mysql.core.Result;
import org.gongxuanzhang.mysql.entity.DeleteInfo;
import org.gongxuanzhang.mysql.entity.InsertInfo;
import org.gongxuanzhang.mysql.entity.SelectInfo;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.entity.UpdateInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.storage.fool.FoolStorageEngine;

/**
 * 存储引擎接口
 * 默认实现有Innodb，Fool
 * 可以自定义
 *
 * @author gxz gongxuanzhang@foxmail.com
 * @see FoolStorageEngine
 * @see InnoDb
 **/
@DependOnContext
public interface StorageEngine {


    /**
     * 引擎名称
     *
     * @return name
     **/
    String getEngineName();

    /**
     * 引擎是否支持事务
     *
     * @return true 是支持
     **/
    boolean supportTransaction();

    /**
     * 建表
     *
     * @param info 表信息
     * @throws MySQLException 执行过程中出现问题抛出异常
     **/
    Result createTable(TableInfo info) throws MySQLException;


    /**
     * 插入数据
     *
     * @param info insert info
     * @throws MySQLException 执行过程中出现问题抛出异常
     **/
    Result insert(InsertInfo info) throws MySQLException;

    /**
     * 删除数据
     *
     * @param info delete info
     * @throws MySQLException 执行过程中出现问题抛出异常
     **/
    Result delete(DeleteInfo info) throws MySQLException;

    /**
     * 修改数据
     *
     * @param info update info
     * @throws MySQLException 执行过程中出现问题抛出异常
     **/
    Result update(UpdateInfo info) throws MySQLException;

    /**
     * 查询数据数据
     *
     * @param info select info
     * @throws MySQLException 执行过程中出现问题抛出异常
     **/
    Result select(SelectInfo info) throws MySQLException;


}
