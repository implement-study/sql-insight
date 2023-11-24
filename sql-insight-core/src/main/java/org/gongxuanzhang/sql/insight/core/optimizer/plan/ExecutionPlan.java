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

import org.gongxuanzhang.sql.insight.core.engine.json.ExecuteEngine;
import org.gongxuanzhang.sql.insight.core.engine.storage.StorageEngine;

/**
 * hand out to {@link StorageEngine} from {@link ExecuteEngine}
 * can also be executed directly from the execute engine
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public interface ExecutionPlan {


    /**
     * when sql start with explain
     * show the execute plan to client
     *
     * @return explain result
     **/
    String showExplain();

    /**
     * @return plan chain
     **/
    PlanChain getPlanChain();

    String getOrginalSql();


}
