package org.gongxuanzhang.mysql.storage.fool;

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.DeleteInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.storage.DeleteEngine;

/**
 * fool 引擎的删除操作
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class FoolDelete implements DeleteEngine {
    @Override
    public Result delete(DeleteInfo info) throws MySQLException {
        return null;
    }
}
