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

package org.gongxuanzhang.mysql.core;

import com.alibaba.fastjson2.JSONObject;
import org.gongxuanzhang.mysql.annotation.DependOnContext;
import org.gongxuanzhang.mysql.entity.Cell;
import org.gongxuanzhang.mysql.entity.IncrementKey;
import org.gongxuanzhang.mysql.entity.InsertData;
import org.gongxuanzhang.mysql.entity.InsertInfo;
import org.gongxuanzhang.mysql.entity.InsertRow;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 辅助列解析
 * 为行提供复杂度更低组合方案
 *
 * @author gongxuanzhang
 */
@DependOnContext
@Deprecated
public class ColumnAdjust {

    private final Map<Integer, String> indexColMap;

    private final IncrementKey incrementKey;

    private final Map<String, Object> defaultValue;

    private final InsertInfo insertInfo;

    private final TableInfo tableInfo;

    /**
     * @param insertInfo 插入信息
     */
    public ColumnAdjust(InsertInfo insertInfo) {
        this.insertInfo = insertInfo;
        this.tableInfo = insertInfo.getTableInfo();
        //  List<String> colList = insertInfo.getColumns();
        List<String> colList = new ArrayList<>();
        this.indexColMap = new HashMap<>(colList.size());
        for (int i = 0; i < colList.size(); i++) {
            this.indexColMap.put(i, colList.get(i));
        }
        this.incrementKey = tableInfo.getIncrementKey();
        this.defaultValue = new HashMap<>();
        tableInfo.getColumns().stream()
                .filter(columnInfo -> columnInfo.getDefaultValue() != null)
                .forEach((columnInfo -> defaultValue.put(columnInfo.getName(), columnInfo.getDefaultValue())));

    }

    public InsertData adjust() throws MySQLException {
        List<JSONObject> insertBox = new ArrayList<>();
        List<InsertRow> allInputRow = insertInfo.getInsertData();
        //  填充用户内容
        for (InsertRow inputRow : allInputRow) {
            insertBox.add(fillInputData(inputRow));
        }
        //  填充自增主键
        if (haveIncrementKey()) {
            for (JSONObject row : insertBox) {
                incrementKey(row);
            }
        }
        //  填充默认值
        if (haveDefaultValue()) {
            for (JSONObject box : insertBox) {
                fillDefault(box);
            }
        }
        return new InsertData(insertBox, tableInfo);
    }

    /**
     * 填充基本输入数据
     *
     * @param inputRow 输入数据
     * @return 返回填充之后的一行数据
     */
    public JSONObject fillInputData(InsertRow inputRow) throws MySQLException {
        JSONObject rowData = new JSONObject();
        List<Cell<?>> cellList = inputRow.getCellList();
        for (int i = 0; i < cellList.size(); i++) {
            Cell<?> cell = cellList.get(i);
            rowData.put(indexColMap.get(i), cell.getValue());
        }
        return rowData;
    }

    /**
     * 是否有自增键
     *
     * @return true是有 false是没有
     */
    public boolean haveIncrementKey() {
        return incrementKey != null;
    }

    public boolean haveDefaultValue() {
        return !this.defaultValue.isEmpty();
    }

    public JSONObject incrementKey(JSONObject jsonObject) {
        if (!haveIncrementKey() || jsonObject.containsKey(this.incrementKey.getColName())) {
            return jsonObject;
        }
        jsonObject.put(incrementKey.getColName(), incrementKey.nextKey());
        return jsonObject;
    }

    public JSONObject fillDefault(JSONObject jsonObject) {
        defaultValue.forEach(jsonObject::putIfAbsent);
        return jsonObject;
    }

}
