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
import org.gongxuanzhang.sql.insight.core.exception.SqlAnalysisException;

/**
 * like mysql query optimizer.
 * but implementation is so difficult.
 * perhaps this optimizer only a very simple function
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public interface Optimizer extends Analyzer {


    /**
     * analysis sql to a wrapped sql
     * <p>
     * <p>
     * {@link org.gongxuanzhang.sql.insight.core.analysis.Analyzer}
     *
     * @return the command
     **/
    Command analysisSql(String sql) throws SqlAnalysisException;

    /**
     * make a plan
     *
     * @param command from sql
     * @return execute plan
     **/
    ExecutionPlan assign(Command command);


}
