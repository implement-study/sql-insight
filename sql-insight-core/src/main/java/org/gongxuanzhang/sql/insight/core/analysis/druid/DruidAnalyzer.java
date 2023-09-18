/*
 * Copyright 2023 java-mysql  and the original author or authors <gongxuanzhangmelt@gmail.com>.
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

package org.gongxuanzhang.sql.insight.core.analysis.druid;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlStatement;
import org.gongxuanzhang.sql.insight.core.analysis.Analyzer;
import org.gongxuanzhang.sql.insight.core.command.Command;
import org.gongxuanzhang.sql.insight.core.exception.NotSupportSqlTypeException;
import org.gongxuanzhang.sql.insight.core.exception.SqlAnalysisException;

import java.util.HashMap;
import java.util.Map;

/**
 * delegate to druid
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public class DruidAnalyzer implements Analyzer {


    private final Map<Class<? extends MySqlStatement>, DruidStatementAdaptor> adaptorMap = new HashMap<>();

    {

    }

    @Override
    public Command analysisSql(String sql) throws SqlAnalysisException {
        SQLStatement sqlStatement = SQLUtils.parseSingleMysqlStatement(sql);
        DruidStatementAdaptor druidStatementAdaptor = adaptorMap.get(sqlStatement.getClass());
        if (druidStatementAdaptor == null) {
            throw new NotSupportSqlTypeException(sql, sqlStatement.getClass());
        }
        return druidStatementAdaptor.adaptor((MySqlStatement) sqlStatement);
    }

    private void registerAdaptor(DruidStatementAdaptor adaptor) {
        DruidStatementAdaptor validate = adaptorMap.putIfAbsent(adaptor.supportType(), adaptor);
        if (validate != adaptor) {
            throw new IllegalArgumentException(adaptor.supportType() + "duplicate type");
        }
    }

}
