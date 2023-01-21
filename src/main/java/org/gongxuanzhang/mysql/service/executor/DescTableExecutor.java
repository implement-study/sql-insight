package org.gongxuanzhang.mysql.service.executor;

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.core.manager.TableManager;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.ExecuteException;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.tool.Context;

/**
 * 展示表结构
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class DescTableExecutor implements Executor {

    private static final String[] TABLE_DESC_HEAD = new String[]{
            "field", "type", "null", "primary key", "default", "auto_increment"};

    private final TableInfo info;

    public DescTableExecutor(TableInfo info) {
        this.info = info;
    }

    @Override
    public Result doExecute() throws MySQLException {
        TableManager tableManager = Context.getTableManager();
        TableInfo select = tableManager.select(info.absoluteName());
        if (select == null) {
            throw new ExecuteException(String.format("表%s不存在", info.getTableName()));
        }
        return Result.select(TABLE_DESC_HEAD, select.descTable());
    }
}
