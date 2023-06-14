/*
 * Copyright 2023 sql-insight  and the original author or authors <gongxuanzhangmelt@gmail.com>.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/implement-study/sql-insight/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gongxuanzhang.mysql.service.analysis.session;

import org.gongxuanzhang.mysql.entity.ShowVarInfo;
import org.gongxuanzhang.mysql.exception.SqlAnalysisException;
import org.gongxuanzhang.mysql.service.analysis.TokenAnalysis;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.executor.session.show.DatabaseShower;
import org.gongxuanzhang.mysql.service.executor.session.show.EngineShower;
import org.gongxuanzhang.mysql.service.executor.session.show.TablesShower;
import org.gongxuanzhang.mysql.service.executor.session.show.VariablesShower;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.TokenKind;
import org.gongxuanzhang.mysql.service.token.TokenSupport;

import java.util.List;

import static org.gongxuanzhang.mysql.tool.ExceptionThrower.ifNotThrow;

/**
 * show 解析器
 * show tables;
 * show database;
 * show session variables;
 * show global variables;
 * show variables;
 * show columns from tableName; (todo)
 * show index from tableName (todo)
 * show grants for user (todo)
 * SHOW CREATE DATABASE db_name (todo)
 * SHOW CREATE TABLE tbl_name(todo)
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class ShowAnalysis implements TokenAnalysis {


    @Override
    public Executor analysis(List<SqlToken> sqlTokenList) throws SqlAnalysisException {
        switch (sqlTokenList.get(1).getTokenKind()) {
            case TABLES:
                checkSize(2, sqlTokenList);
                return new TablesShower();
            case DATABASES:
                checkSize(2, sqlTokenList);
                return new DatabaseShower();
            case ENGINES:
                checkSize(2, sqlTokenList);
                return new EngineShower();
            case SESSION:
                //  todo 变量相关是可以加where的 但是现在还不支持
                return sessionShower(sqlTokenList);
            case GLOBAL:
                return globalShower(sqlTokenList);
            case VARIABLES:
                return variablesShower(sqlTokenList);
            default:
                throw new SqlAnalysisException("不支持的" + sqlTokenList.get(1).getValue());
        }
    }

    private void checkSize(int expectSize, List<SqlToken> sqlTokenList) throws SqlAnalysisException {
        ifNotThrow(sqlTokenList.size() == expectSize, sqlTokenList.get(1));
    }

    private Executor sessionShower(List<SqlToken> sqlTokenList) throws SqlAnalysisException {
        checkSize(3, sqlTokenList);
        ShowVarInfo showVarInfo = new ShowVarInfo();
        showVarInfo.setSession(true);
        TokenSupport.mustTokenKind(sqlTokenList.get(2), TokenKind.VARIABLES);
        return new VariablesShower(showVarInfo);
    }

    private Executor globalShower(List<SqlToken> sqlTokenList) throws SqlAnalysisException {
        checkSize(3, sqlTokenList);
        ShowVarInfo showVarInfo = new ShowVarInfo();
        showVarInfo.setGlobal(true);
        TokenSupport.mustTokenKind(sqlTokenList.get(2), TokenKind.VARIABLES);
        return new VariablesShower(showVarInfo);
    }

    private Executor variablesShower(List<SqlToken> sqlTokenList) throws SqlAnalysisException {
        checkSize(2, sqlTokenList);
        return new VariablesShower(new ShowVarInfo());
    }


}
