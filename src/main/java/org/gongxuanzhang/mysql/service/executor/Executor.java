package org.gongxuanzhang.mysql.service.executor;

import org.gongxuanzhang.mysql.service.Result;
import org.gongxuanzhang.mysql.storage.StorageEngine;

/**
 * 执行器，一般是解析Sql得到的
 *
 * @author gxz gongxuanzhang@foxmail.com
 * @see org.gongxuanzhang.mysql.service.parser.SqlParser
 **/
public interface Executor {

    /**
     * 执行
     *
     * @param storageEngine 对应的存储引擎
     * @return 返回通用信息
     **/
    Result doExecute(StorageEngine storageEngine);
}
