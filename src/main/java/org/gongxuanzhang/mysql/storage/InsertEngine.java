package org.gongxuanzhang.mysql.storage;

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.InsertInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;

/**
 *
 * 插入引擎
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface InsertEngine {

    /**
     * 插入数据
     *
     * @param info insert info
     * @throws MySQLException 执行过程中出现问题抛出异常
     * @return 通用返回
     **/
    Result insert(InsertInfo info) throws MySQLException;
}
