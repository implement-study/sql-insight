package org.gongxuanzhang.mysql.storage;

import org.gongxuanzhang.mysql.entity.DeleteInfo;
import org.gongxuanzhang.mysql.entity.InsertInfo;
import org.gongxuanzhang.mysql.entity.SelectInfo;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.entity.UpdateInfo;
import org.gongxuanzhang.mysql.exception.EngineException;
import org.gongxuanzhang.mysql.storage.fool.FoolStorageEngine;
import org.gongxuanzhang.mysql.storage.innodb.InnoDb;

/**
 * 存储引擎接口
 * 默认实现有Innodb，Fool
 * 可以自定义
 *
 * @author gxz gongxuanzhang@foxmail.com
 * @see FoolStorageEngine
 * @see InnoDb
 **/
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
     * @throws EngineException 执行过程中出现问题抛出异常
     **/
    void createTable(TableInfo info) throws EngineException;


    /**
     * 插入数据
     *
     * @param info insert info
     * @throws EngineException 执行过程中出现问题抛出异常
     **/
    void insert(InsertInfo info) throws EngineException;

    /**
     * 删除数据
     *
     * @param info delete info
     * @throws EngineException 执行过程中出现问题抛出异常
     **/
    void delete(DeleteInfo info) throws EngineException;

    /**
     * 修改数据
     *
     * @param info update info
     * @throws EngineException 执行过程中出现问题抛出异常
     **/
    void update(UpdateInfo info) throws EngineException;

    /**
     * 查询数据数据
     *
     * @param info select info
     * @throws EngineException 执行过程中出现问题抛出异常
     **/
    void select(SelectInfo info) throws EngineException;


}
