package org.gongxuanzhang.mysql.storage;

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.SelectInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;

/**
 * 查询引擎
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface SelectEngine {

    /**
     * 查询数据数据
     *
     * @param info select info
     * @return 通用返回
     * @throws MySQLException 执行过程中出现问题抛出异常
     **/
    Result select(SelectInfo info) throws MySQLException;
}
