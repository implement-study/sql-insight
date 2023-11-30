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

import org.gongxuanzhang.sql.insight.core.command.dml.Insert;
import org.gongxuanzhang.sql.insight.core.engine.storage.StorageEngine;
import org.gongxuanzhang.sql.insight.core.environment.ExecuteContext;
import org.gongxuanzhang.sql.insight.core.event.BeforeInsertEvent;
import org.gongxuanzhang.sql.insight.core.event.EventPublisher;
import org.gongxuanzhang.sql.insight.core.object.InsertRow;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class InsertPlanChain implements PlanChain {

    private final Insert insert;

    private final List<PlanNode> insertNode;

    public InsertPlanChain(Insert insert) {
        this.insert = insert;
        this.insertNode = new ArrayList<>();
        fillNode();
    }


    private void fillNode() {
        List<InsertRow> insertRows = this.insert.getInsertRows();
        for (InsertRow insertRow : insertRows) {
            insertNode.add(new InsertPlanNode(insertRow));
        }
    }

    @NotNull
    @Override
    public Iterator<PlanNode> iterator() {
        return insertNode.iterator();
    }


    private class InsertPlanNode implements PlanNode {

        private final InsertRow row;

        InsertPlanNode(InsertRow row) {
            this.row = row;
        }

        @Override
        public boolean withoutStorageEngine() {
            return false;
        }

        @Override
        public String neededStorageEngineName() {
            return insert.getTable().getEngine();
        }

        @Override
        public void doPlan(StorageEngine storageEngine, ExecuteContext context) throws Exception {
            EventPublisher.getInstance().publishEvent(new BeforeInsertEvent(row));
            storageEngine.insert(row);
        }

    }
}
