package org.gongxuanzhang.mysql.storage.fool;

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.UpdateInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.storage.UpdateEngine;

/**
 * fool 引擎的修改方法
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class FoolUpdate implements UpdateEngine {
    @Override
    public Result update(UpdateInfo info) throws MySQLException {
        return null;
    }
}
