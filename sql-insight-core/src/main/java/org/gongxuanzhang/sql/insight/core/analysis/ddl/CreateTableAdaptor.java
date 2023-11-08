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

package org.gongxuanzhang.sql.insight.core.analysis.ddl;

import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import org.gongxuanzhang.sql.insight.core.analysis.druid.DruidStatementAdaptor;
import org.gongxuanzhang.sql.insight.core.command.ddl.CreateTable;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class CreateTableAdaptor implements DruidStatementAdaptor<MySqlCreateTableStatement, CreateTable> {

    @Override
    public Class<MySqlCreateTableStatement> supportType() {
        return MySqlCreateTableStatement.class;
    }

    @Override
    public CreateTable adaptor(String sql, MySqlCreateTableStatement mySqlStatement) {
        return new CreateTable(sql, mySqlStatement);
    }


}
