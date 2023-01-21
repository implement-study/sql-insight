package org.gongxuanzhang.mysql.storage.fool;

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.SelectInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.storage.SelectEngine;

/**
 * fool 的 查询引擎
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class FoolSelect implements SelectEngine {


    @Override
    public Result select(SelectInfo info) throws MySQLException {
        return null;
    }
}
