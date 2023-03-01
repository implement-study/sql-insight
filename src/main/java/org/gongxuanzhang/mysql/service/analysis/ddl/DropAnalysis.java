/*
 * Copyright 2023 java-mysql  and the original author or authors <gongxuanzhangmelt@gmail.com>.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/implement-study/java-mysql/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gongxuanzhang.mysql.service.analysis.ddl;

import org.gongxuanzhang.mysql.entity.DatabaseInfo;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.exception.SqlAnalysisException;
import org.gongxuanzhang.mysql.service.analysis.TokenAnalysis;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.executor.ddl.drop.DropDatabaseExecutor;
import org.gongxuanzhang.mysql.service.executor.ddl.drop.DropTableExecutor;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.TokenSupport;
import org.gongxuanzhang.mysql.tool.Context;
import org.gongxuanzhang.mysql.tool.ExceptionThrower;
import org.gongxuanzhang.mysql.tool.Pair;

import java.util.List;

/**
 * drop 解析器
 * drop table
 * drop database
 * drop procedure(todo)
 * drop function(todo)
 * drop view(todo)
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class DropAnalysis implements TokenAnalysis {


    @Override
    public Executor analysis(List<SqlToken> sqlTokenList) throws MySQLException {
        final int offset = 1;
        SqlToken sqlToken = sqlTokenList.get(offset);
        switch (sqlToken.getTokenKind()) {
            case TABLE:
                return dropTable(sqlTokenList);
            case DATABASE:
                return dropDataBase(sqlTokenList);
            case PROCEDURE:
            case FUNCTION:
            case VIEW:
                throw new SqlAnalysisException("drop " + sqlToken.getTokenKind() + "还没有实现");
            default:
                throw new SqlAnalysisException("[create " + sqlToken.getValue() + "]有问题");
        }
    }

    private Executor dropDataBase(List<SqlToken> sqlTokenList) throws SqlAnalysisException {
        ExceptionThrower.ifNotThrow(sqlTokenList.size() == 3);
        String database = TokenSupport.getMustVar(sqlTokenList.get(2));
        DatabaseInfo databaseInfo = new DatabaseInfo(database);
        return new DropDatabaseExecutor(databaseInfo);
    }

    private Executor dropTable(List<SqlToken> sqlTokenList) throws MySQLException {
        Pair<Integer, TableInfo> pair = TokenSupport.analysisTableInfo(sqlTokenList, 2);
        ExceptionThrower.ifNotThrow(sqlTokenList.size() == pair.getKey() + 2);
        TableInfo select = Context.getTableManager().select(pair.getValue());
        return new DropTableExecutor(select);
    }


}
