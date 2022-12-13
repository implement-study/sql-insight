package org.gongxuanzhang.mysql.service.parser;

import org.gongxuanzhang.mysql.exception.SqlParseException;
import org.gongxuanzhang.mysql.service.executor.Executor;

/**
 * Sql 语法分析器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface SqlParser {

    /**
     * 解析sql成可执行对象
     *
     * @param sql 被解析的sql
     * @return 返回可执行内容 或者直接报错
     * @throws SqlParseException 解析过程中出现问题
     **/
    Executor parse(String sql) throws SqlParseException;

}
