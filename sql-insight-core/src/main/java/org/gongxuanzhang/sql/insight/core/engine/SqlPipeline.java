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

package org.gongxuanzhang.sql.insight.core.engine;

import org.gongxuanzhang.sql.insight.core.command.Command;
import org.gongxuanzhang.sql.insight.core.engine.execute.ExecuteEngine;
import org.gongxuanzhang.sql.insight.core.engine.execute.SqlInsightExecuteEngine;
import org.gongxuanzhang.sql.insight.core.environment.SqlInsightContext;
import org.gongxuanzhang.sql.insight.core.exception.SqlInsightException;
import org.gongxuanzhang.sql.insight.core.optimizer.ExecutionPlan;
import org.gongxuanzhang.sql.insight.core.optimizer.Optimizer;
import org.gongxuanzhang.sql.insight.core.optimizer.OptimizerImpl;
import org.gongxuanzhang.sql.insight.core.result.ResultInterface;

/**
 * sql lifecycle container
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public class SqlPipeline {

    private Optimizer optimizer = new OptimizerImpl();

    private ExecuteEngine executeEngine = new SqlInsightExecuteEngine();

    public ResultInterface doSql(String sql) throws SqlInsightException {


        Command command = optimizer.analysisSql(sql);

        ExecutionPlan plan = optimizer.assign(command);

        return executeEngine.executePlan(plan);
    }

    public Optimizer getOptimizer() {
        return optimizer;
    }

    public SqlPipeline setOptimizer(Optimizer optimizer) {
        this.optimizer = optimizer;
        return this;
    }

    public ExecuteEngine getExecuteEngine() {
        return executeEngine;
    }

    public SqlPipeline setExecuteEngine(ExecuteEngine executeEngine) {
        this.executeEngine = executeEngine;
        return this;
    }

    private SqlInsightContext createContext(){
        return null;
    }
}
