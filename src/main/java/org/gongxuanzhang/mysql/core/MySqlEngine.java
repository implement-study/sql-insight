package org.gongxuanzhang.mysql.core;


/**
 * 负责执行sql抽象接口
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface MySqlEngine {

    /**
     * 执行sql
     *
     * @param sql sql
     * @return 返回错误内容
     **/
    Result doSql(String sql);

}
