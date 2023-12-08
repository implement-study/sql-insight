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

import org.gongxuanzhang.sql.insight.core.object.value.Value;

import java.util.List;


/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class PhysicRow implements Row, TableContainer {

    private Table table;

    private final List<Value> valueList;

    private final long rowId;

    public PhysicRow(List<Value> valueList, long rowId) {
        this.rowId = rowId;
        this.valueList = valueList;
    }


    @Override
    public List<Value> getValues() {
        return valueList;
    }

    @Override
    public long getRowId() {
        return this.rowId;
    }


    @Override
    public Value getValueByColumnName(String colName) {
        Integer columnIndexByName = table.getColumnIndexByName(colName);
        return this.valueList.get(columnIndexByName);
    }

    @Override
    public Table belongTo() {
        return this.table;
    }


    @Override
    public Table getTable() {
        return belongTo();
    }

    @Override
    public void setTable(Table table) {
        this.table = table;
    }
}
