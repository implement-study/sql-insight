package org.gongxuanzhang.mysql.storage;

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.DeleteInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;

/**
 * 删除引擎
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface DeleteEngine {

    /**
     * 删除数据
     *
     * @param info delete info
     * @return 通用返回
     * @throws MySQLException 执行过程中出现问题抛出异常
     **/
    Result delete(DeleteInfo info) throws MySQLException;
}
