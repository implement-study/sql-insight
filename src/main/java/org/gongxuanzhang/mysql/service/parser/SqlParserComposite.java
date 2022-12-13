package org.gongxuanzhang.mysql.service.parser;

import org.gongxuanzhang.mysql.annotation.SQLParser;
import org.gongxuanzhang.mysql.exception.SqlParseException;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.tool.LRUCache;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * sql解析器组合
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Component
@Primary
public class SqlParserComposite implements SqlParser {

    private final List<SmartSqlParser> parserList;

    private final LRUCache<String, SmartSqlParser> cache = new LRUCache<>(20);

    public SqlParserComposite(@SQLParser List<SmartSqlParser> parserList) {
        this.parserList = parserList;
    }


    @Override
    public Executor parse(String sql) throws SqlParseException {
        SmartSqlParser smartSqlParser = cache.get(sql);
        if (smartSqlParser == null) {
            smartSqlParser = findSupportParser(sql);
        }
        if (smartSqlParser == null) {
            throw new SqlParseException("sql[" + sql + "]无法解析 可能有问题");
        }
        cache.put(sql, smartSqlParser);
        return smartSqlParser.parse(sql);
    }


    private SmartSqlParser findSupportParser(String sql) {
        if (CollectionUtils.isEmpty(parserList)) {
            return null;
        }
        for (SmartSqlParser smartSqlParser : parserList) {
            if (smartSqlParser.support(sql)) {
                return smartSqlParser;
            }
        }
        return null;
    }

}
