package org.gongxuanzhang.mysql.service.executor;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.mysql.entity.ExecuteInfo;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.SqlParseException;
import org.gongxuanzhang.mysql.service.Result;

/**
 * 建表执行器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
public class TableCreator extends AbstractInfoExecutor<TableInfo> {


    public TableCreator(String sql) throws SqlParseException {
        super(sql);
    }

    public TableCreator(String[] split) throws SqlParseException {
        super(split);
    }

    @Override
    public TableInfo analysisInfo(String[] split) {
        // todo
            return null;
    }


    @Override
    public Result doExecute() {
        // todo
        return null;
    }
}
