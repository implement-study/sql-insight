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

    public AbstractInfoExecutor(String sql) throws SqlParseException {
        this(sql.split("\\s+"));
    }


    public AbstractInfoExecutor(String[] split) throws SqlParseException {
        this.info = analysisInfo(split);
    }

    /**
     * 解析sql变成执行信息
     *
     * @param split sql通过空格切割之后的内容
     * @return 执行信息
     * @throws SqlParseException 解析失败抛异常
     **/
    public abstract INFO analysisInfo(String[] split) throws SqlParseException;

    public INFO getInfo() {
        return info;
    }
}
