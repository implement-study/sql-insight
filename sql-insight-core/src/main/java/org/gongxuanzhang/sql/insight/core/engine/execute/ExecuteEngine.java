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
package org.gongxuanzhang.sql.insight.core.engine.execute;

import org.gongxuanzhang.sql.insight.core.engine.storage.StorageEngine;
import org.gongxuanzhang.sql.insight.core.optimizer.ExecutionPlan;
import org.gongxuanzhang.sql.insight.core.optimizer.Optimizer;
import org.gongxuanzhang.sql.insight.core.result.ResultInterface;
import org.springframework.lang.NonNull;

/**
 * execute engine differ from {@link StorageEngine}
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public interface ExecuteEngine {

    /**
     * execute plan
     *
     * @param plan execution plan from {@link Optimizer}
     * @return return storage engine result in general , if the sql is a dcl,return core executor result.
     * maybe return the error result if an error occurred during the sql
     * process
     **/
    @NonNull
    ResultInterface executePlan(ExecutionPlan plan);

}
