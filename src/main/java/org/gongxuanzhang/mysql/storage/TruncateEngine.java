package org.gongxuanzhang.mysql.storage;

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.TruncateInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;

/**
 * truncate引擎
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface TruncateEngine {

    /**
     * 截断表
     *
     * @param info truncate info
     * @return 通用返回
     * @throws MySQLException 执行过程中出现问题抛出异常
     **/
    Result truncate(TruncateInfo info) throws MySQLException;
}
