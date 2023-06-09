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

package org.gongxuanzhang.mysql.service.analysis.dml;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import org.gongxuanzhang.mysql.entity.InsertInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.analysis.StandaloneSqlAnalysis;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.executor.dml.InsertExecutor;
import org.springframework.stereotype.Component;

/**
 * insert 解析器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Component
public class InsertAnalysis implements StandaloneSqlAnalysis {


    @Override
    public Class<? extends SQLStatement> support() {
        return MySqlInsertStatement.class;
    }

    @Override
    public Executor doAnalysis(SQLStatement sqlStatement) throws MySQLException {
        MySqlInsertStatement insertStatement = (MySqlInsertStatement) sqlStatement;
        InsertInfo info = new InsertInfo(insertStatement);
        return new InsertExecutor(info);
    }
}
