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

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.analysis.StandaloneSqlAnalysis;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.executor.ddl.drop.DropTableExecutor;
import org.gongxuanzhang.mysql.tool.TableInfoUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * drop table 解析器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Component
public class DropTableAnalysis implements StandaloneSqlAnalysis {



    @Override
    public Class<? extends SQLStatement> support() {
        return SQLDropTableStatement.class;
    }

    @Override
    public Executor doAnalysis(SQLStatement sqlStatement) throws MySQLException {
        SQLDropTableStatement statement = (SQLDropTableStatement) sqlStatement;
        List<TableInfo> tableInfos = TableInfoUtils.batchSelectTableInfo(statement.getTableSources());
        return new DropTableExecutor(tableInfos);
    }


}
