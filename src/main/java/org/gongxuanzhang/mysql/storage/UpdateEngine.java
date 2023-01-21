package org.gongxuanzhang.mysql.storage;

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.UpdateInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;

/**
 * 修改引擎
 * @author gxz gongxuanzhang@foxmail.com
 *
 **/
public interface UpdateEngine {



    /**
     * 修改数据
     *
     * @param info update info
     * @throws MySQLException 执行过程中出现问题抛出异常
     **/
    Result update(UpdateInfo info) throws MySQLException;
}
