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

package org.gongxuanzhang.sql.insight.core.command.dml;

import org.gongxuanzhang.sql.insight.core.command.Command;
import org.gongxuanzhang.sql.insight.core.optimizer.plan.ExecutionPlan;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public interface DmlCommand extends Command {


    /**
     * DML commands depend on storage engine
     *
     * @return always false
     **/
    @Override
    default boolean directly() {
        return false;
    }

    /**
     * package to execution plan
     * @return 返回个啥
     **/
    ExecutionPlan plan();

}
