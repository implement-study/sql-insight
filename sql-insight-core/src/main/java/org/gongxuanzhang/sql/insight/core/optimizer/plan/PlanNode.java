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

package org.gongxuanzhang.sql.insight.core.optimizer.plan;

import org.gongxuanzhang.sql.insight.core.engine.storage.StorageEngine;
import org.gongxuanzhang.sql.insight.core.environment.ExecuteContext;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public interface PlanNode {


    /**
     * in general,return true when the sql is ddl,because ddl can be execute without storage engine .
     *
     * @return true can be without storage engine
     **/
    boolean withoutStorageEngine();


    /**
     * engine name needed for this plan
     *
     * @return if {@link this#withoutStorageEngine()} is true, ignore the method , can return null
     **/
    String needStorageEngineName();

    /**
     * do the plan
     *
     * @param storageEngine if {@link this#withoutStorageEngine()} is true, the param is null
     *                      else is engine what is plan node needed
     * @param context       execute context ,sharded in chain
     **/
    void doPlan(StorageEngine storageEngine, ExecuteContext context);

}
