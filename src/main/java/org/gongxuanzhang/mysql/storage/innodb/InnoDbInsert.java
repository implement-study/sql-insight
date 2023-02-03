package org.gongxuanzhang.mysql.storage.innodb;

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.InsertInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.storage.InsertEngine;

/**
 * innodb 引擎的插入模板
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class InnoDbInsert implements InsertEngine {

    @Override
    public Result insert(InsertInfo info) throws MySQLException {
        return null;
    }

}
