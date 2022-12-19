package org.gongxuanzhang.mysql.service.parser;

import org.gongxuanzhang.mysql.exception.SqlParseException;
import org.gongxuanzhang.mysql.service.executor.Executor;

/**
 * 拓展sql Parser
 * 通过sql的前缀判断是否支持，如果支持再解析
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface SmartSqlParser extends SqlParser {

    /**
     * 支持的sql
     *
     * @param sql sql
     * @return true 为支持 false 不支持
     **/
    default boolean support(String sql) {
        return sql.toLowerCase().startsWith(prefix().toLowerCase());
    }


    /**
     * 匹配的前缀
     *
     * @return 前缀
     **/
    String prefix();

    /**
     * 把sql通过空格切割之后 更容易解析
     *
     * @param split 通过空格切割之后的数组
     * @param sql   原sql
     * @return 返回执行器
     * @throws SqlParseException sql解析错误异常
     **/
    Executor parse(String[] split, String sql) throws SqlParseException;

    /**
     * 默认实现父接口的解析方法，改变参数为字符串数组
     *
     * @param sql sql
     * @return 返回执行器
     * @throws SqlParseException sql解析错误异常
     **/
    @Override
    default Executor parse(String sql) throws SqlParseException {
        String[] split = sql.split(" ");
        return parse(split, sql);
    }
}
