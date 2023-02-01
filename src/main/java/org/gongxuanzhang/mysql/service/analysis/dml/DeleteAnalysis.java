package org.gongxuanzhang.mysql.service.analysis.dml;

import org.gongxuanzhang.mysql.entity.DeleteInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.analysis.TokenAnalysis;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.executor.dml.DeleteExecutor;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.TokenSupport;
import org.gongxuanzhang.mysql.storage.StorageEngine;
import org.gongxuanzhang.mysql.tool.Context;

import java.util.List;

/**
 * delete 解析器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class DeleteAnalysis implements TokenAnalysis {


    @Override
    public Executor analysis(List<SqlToken> sqlTokenList) throws MySQLException {
        DeleteInfo deleteInfo = new DeleteInfo();
        int offset = 1;
        offset += TokenSupport.fillFrom(deleteInfo, sqlTokenList.subList(1, sqlTokenList.size()));
        TokenSupport.fillWhere(deleteInfo, sqlTokenList.subList(offset, sqlTokenList.size()));
        StorageEngine engine = Context.selectStorageEngine(deleteInfo.getFrom().getTableInfo().getEngineName());
        return new DeleteExecutor(engine, deleteInfo);
    }


}
