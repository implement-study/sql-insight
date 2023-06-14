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

package org.gongxuanzhang.mysql.service.analysis.dml;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import org.gongxuanzhang.mysql.entity.SingleSelectInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.analysis.StandaloneSqlAnalysis;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.executor.dml.SingleSelectExecutor;
import org.springframework.stereotype.Component;

/**
 * select 解析器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Component
public class SelectAnalysis implements StandaloneSqlAnalysis {


    @Override
    public Class<? extends SQLStatement> support() {
        return SQLSelectStatement.class;
    }

    @Override
    public Executor doAnalysis(SQLStatement sqlStatement) throws MySQLException {
        SQLSelectStatement selectStatement = (SQLSelectStatement) sqlStatement;
        SQLSelect select = selectStatement.getSelect();
        MySqlSelectQueryBlock query = (MySqlSelectQueryBlock) select.getQueryBlock();
        SQLTableSource from = query.getFrom();
        if (from instanceof SQLExprTableSource) {
            return new SingleSelectExecutor(singleSelectInfo(query));
        }
        throw new UnsupportedOperationException("还不支持连表查询");
    }

    private SingleSelectInfo singleSelectInfo(MySqlSelectQueryBlock sqlSelectQueryBlock) throws MySQLException {
        return new SingleSelectInfo(sqlSelectQueryBlock);
    }
}
