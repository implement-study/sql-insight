package org.gongxuanzhang.mysql.service.executor;

import org.gongxuanzhang.mysql.core.Result;
import org.gongxuanzhang.mysql.exception.MySQLException;

/**
 * 执行器，一般是解析Sql得到的
 *
 * @author gxz gongxuanzhang@foxmail.com
 * @see org.gongxuanzhang.mysql.service.analysis.SqlAnalysis
 * @see org.gongxuanzhang.mysql.service.analysis.TokenAnalysis
 **/
public interface Executor {

    /**
     * 执行
     *
     * @return 返回执行结果
     * @throws MySQLException 执行过程中出现问题抛出异常
     **/
    Result doExecute() throws MySQLException;
}
