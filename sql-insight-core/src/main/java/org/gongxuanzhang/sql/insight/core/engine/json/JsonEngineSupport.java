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

package org.gongxuanzhang.sql.insight.core.engine.json;

import com.alibaba.fastjson2.JSONObject;
import org.gongxuanzhang.sql.insight.core.exception.DateTypeCastException;
import org.gongxuanzhang.sql.insight.core.object.Column;
import org.gongxuanzhang.sql.insight.core.object.DataType;
import org.gongxuanzhang.sql.insight.core.object.DeleteRow;
import org.gongxuanzhang.sql.insight.core.object.Table;
import org.gongxuanzhang.sql.insight.core.object.value.Value;
import org.gongxuanzhang.sql.insight.core.object.value.ValueInt;
import org.gongxuanzhang.sql.insight.core.object.value.ValueNull;
import org.gongxuanzhang.sql.insight.core.object.value.ValueVarchar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * some static method support json engine
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public class JsonEngineSupport {
    private JsonEngineSupport() {

    }


    /**
     * get a primary key from json.
     * the return type definitely int
     *
     * @param table table info
     * @param json  insert row
     * @return perhaps null
     **/
    static Integer getJsonInsertRowPrimaryKey(Table table, JSONObject json) {
        List<Column> columnList = table.getColumnList();
        Column column = columnList.get(table.getPrimaryKeyIndex());
        return json.getInteger(column.getName());
    }

    /**
     * get json data file
     *
     * @param table table
     * @return file perhaps not exists
     **/
    static File getJsonFile(Table table) {
        return new File(table.getDatabase().getDbFolder(), table.getName() + ".json");
    }

    static DeleteRow getDeleteRowFromJsonLine(String jsonLine, Table table) {
        JSONObject jsonObject = JSONObject.parseObject(jsonLine);
        Column primaryKey = table.getColumnList().get(table.getPrimaryKeyIndex());
        long id = jsonObject.getLongValue(primaryKey.getName());
        List<Value> valueList = new ArrayList<>(table.getColumnList().size());
        for (Column column : table.getColumnList()) {
            valueList.add(wrapValue(column, jsonObject.get(column.getName())));
        }
        DeleteRow deleteRow = new DeleteRow(valueList, id);
        deleteRow.setTable(table);
        return deleteRow;
    }

    private static Value wrapValue(Column column, Object o) {
        DataType.Type type = column.getDataType().getType();
        if (type == DataType.Type.INT) {
            return new ValueInt((int) o);
        }
        if (type == DataType.Type.VARCHAR || type == DataType.Type.CHAR) {
            return new ValueVarchar(o.toString());
        }
        if (o == null) {
            return column.getDefaultValue() == null ? ValueNull.getInstance() : column.getDefaultValue();
        }
        throw new DateTypeCastException(column.getDataType().toString(), o.toString());
    }


}
