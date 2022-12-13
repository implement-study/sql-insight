package org.gongxuanzhang.mysql.service.parser;

/**
 * 拓展sql Parser
 * 先判断某sql是否支持 再解析
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
    boolean support(String sql);
}
