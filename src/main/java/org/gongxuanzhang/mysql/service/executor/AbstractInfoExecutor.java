package org.gongxuanzhang.mysql.service.executor;

import org.gongxuanzhang.mysql.entity.ExecuteInfo;
import org.gongxuanzhang.mysql.exception.SqlParseException;

/**
 * 可以解析sql成执行信息
 * 同时自己也是一个执行器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public abstract class AbstractInfoExecutor<INFO extends ExecuteInfo> implements Executor {


    protected final INFO info;

    protected final String sql;

    public AbstractInfoExecutor(String sql) throws SqlParseException {
        this.sql = sql;
        this.info = analysisInfo(sql);
    }


    /**
     * 解析sql变成执行信息
     *
     * @param sql sql通过空格切割之后的内容
     * @return 执行信息
     * @throws SqlParseException 解析失败抛异常
     **/
    public abstract INFO analysisInfo(String sql) throws SqlParseException;

    public INFO getInfo() {
        return info;
    }
}
