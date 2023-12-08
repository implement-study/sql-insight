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

import org.gongxuanzhang.sql.insight.core.command.dml.Select;
import org.gongxuanzhang.sql.insight.core.engine.storage.StorageEngine;
import org.gongxuanzhang.sql.insight.core.environment.ExecuteContext;
import org.gongxuanzhang.sql.insight.core.object.Cursor;
import org.gongxuanzhang.sql.insight.core.object.Index;
import org.gongxuanzhang.sql.insight.core.object.Limit;
import org.gongxuanzhang.sql.insight.core.object.Row;
import org.gongxuanzhang.sql.insight.core.object.Table;
import org.gongxuanzhang.sql.insight.core.object.Where;

import java.util.List;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class SelectPlanNode implements PlanNode {


    private Table table;

    private Select select;

    public SelectPlanNode(Table table, Select select) {
        this.table = table;
        this.select = select;
    }

    @Override
    public boolean withoutStorageEngine() {
        return false;
    }

    @Override
    public String neededStorageEngineName() {
        return table.getEngine();
    }

    @Override
    public void doPlan(StorageEngine storageEngine, ExecuteContext context) throws Exception {
        storageEngine.openTable(this.table);
        List<Index> indexList = this.table.getIndexList();
        //  decide index
        Index main = indexList.get(0);
        main.rndInit();
        Where where = this.select.getWhere();
        Cursor cursor = main.find(context.getSessionContext());
        Limit limit = selectLimit();
        int skipped = 0;
        int rowCount = 0;
        while (rowCount < limit.getRowCount() && cursor.hasNext()) {
            Row next = cursor.next();
            if (Boolean.TRUE.equals(where.getBooleanValue(next))) {
                if (skipped != limit.getSkip()) {
                    skipped++;
                    continue;
                }
                rowCount++;
                context.addRow(next);
            }
        }
        cursor.close();

    }

    private Limit selectLimit() {
        Limit limit = new Limit();
        if (this.select.getLimit() != null) {
            limit.setSkip(this.select.getLimit().getSkip());
            limit.setRowCount(this.select.getLimit().getRowCount());
        }
        if (this.select.getOrderBy() != null) {
            limit.setRowCount(Integer.MAX_VALUE);
        }
        return limit;
    }

}
