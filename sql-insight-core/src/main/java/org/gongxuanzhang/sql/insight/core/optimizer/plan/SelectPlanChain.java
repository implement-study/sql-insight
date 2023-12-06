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
import org.gongxuanzhang.sql.insight.core.object.Table;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class SelectPlanChain implements PlanChain {


    private final Select select;

    private final List<PlanNode> selectNodes = new ArrayList<>();

    public SelectPlanChain(Select select) {
        this.select = select;
        initPlanNode();
    }

    private void initPlanNode() {
        List<Table> tableList = select.getTableList();
        if (tableList.size() > 1) {
            throw new UnsupportedOperationException("join support in to-do list");
        }
        Table table = tableList.get(0);
        this.selectNodes.add(new SelectPlanNode(table, select.getWhere()));
    }


    @NotNull
    @Override
    public Iterator<PlanNode> iterator() {
        return this.selectNodes.iterator();
    }
}
