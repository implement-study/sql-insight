package org.gongxuanzhang.mysql.service.parser;

import org.gongxuanzhang.mysql.exception.SqlParseException;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.tool.LRUCache;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * sql解析器组合
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Component
public class SqlParserComposite implements SmartSqlParser {

    @Nullable
    private List<SmartSqlParser> parserList;

    @PostConstruct
    public void a() {
        this.addParser(new SelectSqlParser());
    }

    private final LRUCache<String, SmartSqlParser> cache = new LRUCache<>(20);

    @Override
    public boolean support(String sql) {
        if (CollectionUtils.isEmpty(parserList)) {
            return false;
        }
        for (SmartSqlParser smartSqlParser : parserList) {
            if (smartSqlParser.support(sql)) {
                cache.put(sql, smartSqlParser);
                return true;
            }
        }
        return false;
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


    public void setParserList(@Nullable List<SmartSqlParser> parserList) {
        this.parserList = parserList;
    }

    public void addParser(SmartSqlParser parser) {
        if (this.parserList == null) {
            this.parserList = new ArrayList<>();
        }
        this.parserList.add(parser);
    }
}
