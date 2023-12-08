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

import org.gongxuanzhang.sql.insight.core.command.dml.Delete;
import org.gongxuanzhang.sql.insight.core.engine.storage.StorageEngine;
import org.gongxuanzhang.sql.insight.core.environment.ExecuteContext;
import org.gongxuanzhang.sql.insight.core.object.Cursor;
import org.gongxuanzhang.sql.insight.core.object.Index;
import org.gongxuanzhang.sql.insight.core.object.Row;
import org.gongxuanzhang.sql.insight.core.object.Table;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class DeletePlanChain extends SingleStoragePlanChain {


    private final Delete delete;

    public DeletePlanChain(Delete delete) {
        super(delete.getTable().getEngine());
        this.delete = delete;
    }


    @Override
    protected void doPlan(StorageEngine storageEngine, ExecuteContext context) {
        Table table = delete.getTable();
        storageEngine.openTable(table);
        Index main = table.getIndexList().get(0);
        main.rndInit();
        Cursor cursor = main.find(context.getSessionContext());
        while (cursor.hasNext()) {
            Row next = cursor.next();
            if(Boolean.TRUE.equals(this.delete.getWhere().getBooleanValue(next))){
                storageEngine.delete(next);
            }
        }
    }
}
