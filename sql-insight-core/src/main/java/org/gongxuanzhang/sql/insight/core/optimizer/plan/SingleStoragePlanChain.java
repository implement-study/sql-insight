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
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;

/**
 * plan chain that have only one plan node。
 * use for storage engine plan. like dml
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public abstract class SingleStoragePlanChain implements PlanChain {

    private final String engineName;


    protected SingleStoragePlanChain(String engineName) {
        this.engineName = engineName;
    }


    @NotNull
    @Override
    public Iterator<PlanNode> iterator() {
        return new SingleIterator(new SingleStoragePlanNode(this::doPlan));
    }

    /**
     * delegate method
     **/
    protected abstract void doPlan(StorageEngine storageEngine, ExecuteContext context);

    private class SingleIterator implements Iterator<PlanNode> {

        private SingleStoragePlanNode node;

        public SingleIterator(SingleStoragePlanNode node) {
            this.node = node;
        }

        @Override
        public boolean hasNext() {
            return node != null;
        }

        @Override
        public PlanNode next() {
            SingleStoragePlanNode result = node;
            if (result == null) {
                throw new NoSuchElementException();
            }
            node = null;
            return result;
        }
    }


    private class SingleStoragePlanNode implements PlanNode {

        private final BiConsumer<StorageEngine, ExecuteContext> doPlan;

        SingleStoragePlanNode(BiConsumer<StorageEngine, ExecuteContext> doPlan) {
            this.doPlan = doPlan;
        }

        @Override
        public boolean withoutStorageEngine() {
            return false;
        }

        @Override
        public String neededStorageEngineName() {
            return SingleStoragePlanChain.this.engineName;
        }

        @Override
        public void doPlan(StorageEngine storageEngine, ExecuteContext context) throws Exception {
            doPlan.accept(storageEngine, context);
        }
    }
}
