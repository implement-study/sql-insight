package org.gongxuanzhang.mysql.storage.fool;

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.TruncateInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.storage.TruncateEngine;

/**
 * fool 引擎的删除表
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class FoolTruncate implements TruncateEngine {


    @Override
    public Result truncate(TruncateInfo info) throws MySQLException {
        //  不用短路
        boolean successDelete = info.getTableInfo().dataFile().delete() & info.getTableInfo().structFile().delete();
        if (successDelete) {
            return Result.info("成功删除" + info.getTableInfo().getTableName() + "表");
        }
        return Result.error("删除表失败");
    }
}
