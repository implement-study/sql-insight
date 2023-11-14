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

package org.gongxuanzhang.sql.insight.core.environment;

import org.gongxuanzhang.sql.insight.core.object.Table;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class TableDefinitionManager {

    private final Map<String, Table> tableMap = new HashMap<>();

    public void reload(Table table) {
        tableMap.put(tableKey(table), table);
    }

    public Table select(String database, String tableName) {
        return tableMap.get(database + "." + tableName);
    }


    private String tableKey(Table table) {
        return table.getDatabase().getName() + "." + table.getName();
    }

}
