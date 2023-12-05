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

package org.gongxuanzhang.sql.insight.core.engine.json;

import org.gongxuanzhang.sql.insight.core.engine.StorageEngineManager;
import org.gongxuanzhang.sql.insight.core.engine.storage.StorageEngine;
import org.gongxuanzhang.sql.insight.core.environment.ExecuteContext;
import org.gongxuanzhang.sql.insight.core.exception.SqlInsightException;
import org.gongxuanzhang.sql.insight.core.optimizer.plan.ExecutionPlan;
import org.gongxuanzhang.sql.insight.core.optimizer.plan.PlanChain;
import org.gongxuanzhang.sql.insight.core.result.ResultInterface;
import org.springframework.lang.NonNull;

/**
 * implementation for execute engine
 * if the execution plan can be executed without storage engine
 * use server engine do it
 * else select storage engine do it
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public class SqlInsightExecuteEngine implements ExecuteEngine {

    private StorageEngineManager storageEngineManager;

    @Override
    @NonNull
    public ResultInterface executePlan(ExecutionPlan plan) {
        PlanChain planChain = plan.getPlanChain();

        ExecuteContext context = new ExecuteContext(plan.getOriginalSql());

        planChain.forEach(node -> {
            StorageEngine engine = null;
            if (!node.withoutStorageEngine()) {
                engine = storageEngineManager.selectEngine(node.neededStorageEngineName());
            }
            try {
                node.doPlan(engine, context);
            } catch (SqlInsightException e) {
                throw e;
            } catch (Exception e) {
                //   todo
                throw new RuntimeException(e);
            }
        });
        return context.toResult();
    }

    public void setStorageEngineManager(StorageEngineManager storageEngineManager) {
        this.storageEngineManager = storageEngineManager;
    }

    public StorageEngineManager getStorageEngineManager() {
        return storageEngineManager;
    }
}
