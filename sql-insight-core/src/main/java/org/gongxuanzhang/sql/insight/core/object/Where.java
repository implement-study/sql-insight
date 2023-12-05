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

package org.gongxuanzhang.sql.insight.core.object;

import org.gongxuanzhang.sql.insight.core.object.condition.AlwaysCondition;
import org.gongxuanzhang.sql.insight.core.object.condition.BooleanExpression;
import org.gongxuanzhang.sql.insight.core.object.value.ValueBoolean;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class Where implements TableContainer, BooleanExpression {

    private Table table;

    private final BooleanExpression condition;

    public Where(boolean always) {
        condition = AlwaysCondition.getInstance(always);
    }

    public Where(BooleanExpression condition) {
        this.condition = condition;
    }

    @Override
    public Table getTable() {
        return table;
    }

    @Override
    public void setTable(Table table) {
        this.table = table;
    }


    @Override
    public ValueBoolean getExpressionValue(Row row) {
        return new ValueBoolean(condition.getBooleanValue(row));
    }
}