package org.gongxuanzhang.mysql.service.analysis.session;

import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.analysis.TokenAnalysis;
import org.gongxuanzhang.mysql.service.executor.DescTableExecutor;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.TokenSupport;
import org.gongxuanzhang.mysql.tool.ExceptionThrower;
import org.gongxuanzhang.mysql.tool.Pair;

import java.util.List;

/**
 * desc or describe 解析器
 * desc tableName
 * describe tableName
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class DescAnalysis implements TokenAnalysis {


    @Override
    public Executor analysis(List<SqlToken> sqlTokenList) throws MySQLException {
        Pair<Integer, TableInfo> pair = TokenSupport.analysisTableInfo(sqlTokenList, 1);
        ExceptionThrower.ifNotThrow(sqlTokenList.size() == pair.getKey() + 1);
        return new DescTableExecutor(pair.getValue());
    }

}
