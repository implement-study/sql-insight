package org.gongxuanzhang.mysql.service.executor;

import org.gongxuanzhang.mysql.core.Result;

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
     * @return 返回执行结果
     **/
    Result doExecute();
}
