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

package org.gongxuanzhang.mysql.core;

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.service.analysis.TokenAnalysis;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.SqlTokenizer;
import org.gongxuanzhang.mysql.tool.SqlUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Component
public class CoreMySqlEngine implements MySqlEngine {

    private final TokenAnalysis tokenAnalysis;

    public CoreMySqlEngine(TokenAnalysis tokenAnalysis) {
        this.tokenAnalysis = tokenAnalysis;
    }

    @Override
    public Result doSql(String sql) {
        try {
            long startTime = System.currentTimeMillis();
            SessionManager.currentSession().setSql(sql);
            SqlTokenizer tokenizer = new SqlTokenizer(sql);
            List<SqlToken> process = tokenizer.process();
            Executor executor = tokenAnalysis.analysis(process);
            Result result = executor.doExecute();
            result.setSqlTime(SqlUtils.sqlTime(startTime));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }

    }
}
