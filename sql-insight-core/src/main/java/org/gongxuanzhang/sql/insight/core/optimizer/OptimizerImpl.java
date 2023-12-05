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

package org.gongxuanzhang.sql.insight.core.optimizer;

import org.gongxuanzhang.sql.insight.core.analysis.Analyzer;
import org.gongxuanzhang.sql.insight.core.command.Command;
import org.gongxuanzhang.sql.insight.core.command.dml.DmlCommand;
import org.gongxuanzhang.sql.insight.core.exception.SqlAnalysisException;
import org.gongxuanzhang.sql.insight.core.optimizer.plan.DirectlyExecutionPlan;
import org.gongxuanzhang.sql.insight.core.optimizer.plan.ExecutionPlan;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class OptimizerImpl implements Optimizer {

    private Analyzer analyzer;

    @Override
    public Command analysisSql(String sql) throws SqlAnalysisException {
        return analyzer.analysisSql(sql);
    }

    @Override
    public ExecutionPlan assign(Command command) {
        if (command.directly()) {
            return new DirectlyExecutionPlan(command);
        }
        if (command instanceof DmlCommand) {
            return ((DmlCommand) command).plan();
        }
        throw new UnsupportedOperationException("dont support explain " + command.getSql());
    }


    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }
}
