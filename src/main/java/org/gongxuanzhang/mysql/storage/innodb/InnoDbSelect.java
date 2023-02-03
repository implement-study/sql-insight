package org.gongxuanzhang.mysql.storage.innodb;

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.SingleSelectInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.storage.SelectEngine;

/**
 * innodb 的 查询引擎
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class InnoDbSelect implements SelectEngine {


    @Override
    public Result select(SingleSelectInfo info) throws MySQLException {
        return null;
    }
}
