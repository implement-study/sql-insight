package org.gongxuanzhang.mysql.service.analysis.ddl;

import org.gongxuanzhang.mysql.entity.TruncateInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.analysis.TokenAnalysis;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.executor.dml.TruncateExecutor;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.TokenKind;
import org.gongxuanzhang.mysql.service.token.TokenSupport;
import org.gongxuanzhang.mysql.storage.StorageEngine;
import org.gongxuanzhang.mysql.tool.Context;
import org.gongxuanzhang.mysql.tool.ExceptionThrower;

import java.util.List;

/**
 * truncate 解析器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class TruncateAnalysis implements TokenAnalysis {


    @Override
    public Executor analysis(List<SqlToken> sqlTokenList) throws MySQLException {
        TruncateInfo truncateInfo = new TruncateInfo();
        TokenSupport.mustTokenKind(sqlTokenList.get(1), TokenKind.TABLE);
        int offset = TokenSupport.fillTableInfo(truncateInfo, sqlTokenList, 2);
        ExceptionThrower.ifNotThrow(sqlTokenList.size() == offset + 2);
        StorageEngine engine = Context.selectStorageEngine(truncateInfo.getTableInfo());
        return new TruncateExecutor(engine, truncateInfo);
    }


}
