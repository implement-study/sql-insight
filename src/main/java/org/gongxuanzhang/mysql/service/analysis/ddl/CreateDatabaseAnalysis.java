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

package org.gongxuanzhang.mysql.service.analysis.ddl;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateDatabaseStatement;
import org.gongxuanzhang.mysql.entity.CreateDatabaseInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.analysis.StandaloneSqlAnalysis;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.executor.ddl.create.CreateDatabaseExecutor;
import org.springframework.stereotype.Component;

/**
 * create database 解析器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Component
public class CreateDatabaseAnalysis implements StandaloneSqlAnalysis {


    @Override
    public Class<? extends SQLStatement> support() {
        return SQLCreateDatabaseStatement.class;
    }

    @Override
    public Executor doAnalysis(SQLStatement sqlStatement) throws MySQLException {
        SQLCreateDatabaseStatement statement = (SQLCreateDatabaseStatement) sqlStatement;
        CreateDatabaseInfo databaseInfo = new CreateDatabaseInfo(statement);
        return new CreateDatabaseExecutor(databaseInfo);
    }


}
