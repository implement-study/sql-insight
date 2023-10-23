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

import org.gongxuanzhang.sql.insight.core.command.Command;
import org.gongxuanzhang.sql.insight.core.engine.storage.StorageEngine;
import org.gongxuanzhang.sql.insight.core.environment.ExecuteContext;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * if command is typically executed directly.
 * use this class
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public class DirectlyExecutionPlan implements ExecutionPlan {

    private final Command command;

    public DirectlyExecutionPlan(Command command) {
        if (!command.directly()) {
            throw new IllegalArgumentException("command must be directly");
        }
        this.command = command;
    }

    @Override
    public String showExplain() {
        return command.getSqlType() + "dont need explain";
    }

    @Override
    public PlanChain getPlanChain() {
        return new SinglePlanChain();
    }

    @Override
    public String getOrginalSql() {
        return this.command.getSql();
    }


    private class SinglePlanChain implements PlanChain {

        @Override
        public void addNode(PlanNode node) {
            throw new UnsupportedOperationException();
        }

        @NotNull
        @Override
        public Iterator<PlanNode> iterator() {
            return new SingleCommandIterator(command);
        }
    }

    private static class SingleCommandIterator implements Iterator<PlanNode> {

        private Command command;

        SingleCommandIterator(Command command) {
            this.command = command;
        }

        @Override
        public boolean hasNext() {
            return this.command != null;
        }

        @Override
        public SingleCommandPlanNode next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Command result = command;
            this.command = null;
            return result::run;
        }
    }

    @FunctionalInterface
    private interface SingleCommandPlanNode extends PlanNode {

        @Override
        default boolean withoutStorageEngine() {
            return true;
        }

        @Override
        default String neededStorageEngineName() {
            return null;
        }

        @Override
        default void doPlan(StorageEngine storageEngine, ExecuteContext context) {
            this.doWithOutEngine(context);
        }

        void doWithOutEngine(ExecuteContext context);

    }
}
