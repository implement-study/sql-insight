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
import org.gongxuanzhang.sql.insight.core.object.Column;
import org.gongxuanzhang.sql.insight.core.object.Table;

import java.io.File;
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


}
