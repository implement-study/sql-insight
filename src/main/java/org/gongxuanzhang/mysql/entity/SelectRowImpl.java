/*
 * Copyright 2023 java-mysql  and the original author or authors <gongxuanzhangmelt@gmail.com>.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/implement-study/java-mysql/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gongxuanzhang.mysql.entity;

import org.gongxuanzhang.mysql.entity.page.Compact;
import org.gongxuanzhang.mysql.entity.page.CompactNullValue;
import org.gongxuanzhang.mysql.entity.page.Variables;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SelectRowImpl implements SelectRow {

    private final TableInfo tableInfo;

    private List<Cell<?>> cellList;

    public SelectRowImpl(TableInfo tableInfo, Compact compact) {
        this.tableInfo = tableInfo;
        this.initCellList(compact);
    }


    private void initCellList(Compact compact) {
        cellList = new ArrayList<>();
        List<Column> columns = tableInfo.getColumns();
        CompactNullValue nullValues = compact.getNullValues();
        Variables variables = compact.getVariables();
        ByteBuffer body = ByteBuffer.wrap(compact.getBody());
        int variableIndex = 0;
        for (Column currentCol : columns) {
            if (!currentCol.isNotNull() && nullValues.isNull(currentCol.getNullIndex())) {
                cellList.add(new NullCell());
                continue;
            }
            if (currentCol.isDynamic()) {
                byte length = variables.get(variableIndex);
                byte[] buffer = new byte[length];
                body.get(buffer);
                String value = new String(buffer);
                cellList.add(new VarcharCell(value));
                variableIndex++;
            } else {
                cellList.add(new IntCell(body.getInt()));
            }
        }
    }

    @Override
    public List<Cell<?>> getCellList() {
        return this.cellList;
    }

    @Override
    public TableInfo getTableInfo() {
        return this.tableInfo;
    }

    @Override
    public Map<String, String> showMap() {
        Map<String, String> result = new LinkedHashMap<>();
        List<Column> columns = this.tableInfo.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            result.put(columns.get(i).getName(), this.cellList.get(i).getValue().toString());
        }
        return result;
    }
}
