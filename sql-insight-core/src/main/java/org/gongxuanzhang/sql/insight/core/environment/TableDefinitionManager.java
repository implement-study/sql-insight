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

import com.google.common.collect.HashBasedTable;
import org.gongxuanzhang.sql.insight.core.event.CreateTableEvent;
import org.gongxuanzhang.sql.insight.core.event.DropDatabaseEvent;
import org.gongxuanzhang.sql.insight.core.event.DropTableEvent;
import org.gongxuanzhang.sql.insight.core.event.InsightEvent;
import org.gongxuanzhang.sql.insight.core.event.MultipleEventListener;
import org.gongxuanzhang.sql.insight.core.object.Database;
import org.gongxuanzhang.sql.insight.core.object.Table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * delegate to guava {@link  com.google.common.collect.Table}
 * row key is table database name
 * col key is table name
 * value is table info object
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public class TableDefinitionManager implements MultipleEventListener {


    private final HashBasedTable<String, String, Table> tableInfoCache = HashBasedTable.create();


    public void load(Table table) {
        tableInfoCache.put(table.getDatabase().getName(), table.getName(), table);
    }

    public void unload(Table table) {
        tableInfoCache.remove(table.getDatabase().getName(), table.getName());
    }

    public void unload(Database database) {
        tableInfoCache.row(database.getName()).clear();
    }

    public Table select(String database, String tableName) {
        return tableInfoCache.get(database, tableName);
    }

    public List<Table> select(String database) {
        return new ArrayList<>(tableInfoCache.row(database).values());
    }

    @Override
    public void onEvent(InsightEvent event) {
        if (event instanceof DropDatabaseEvent) {
            this.unload(((DropDatabaseEvent) event).getDatabase());
            return;
        }
        if (event instanceof CreateTableEvent) {
            this.load(((CreateTableEvent) event).getTable());
            return;
        }
        if (event instanceof DropTableEvent) {
            this.unload(((DropTableEvent) event).getTable());
        }
    }


    @Override
    public List<Class<? extends InsightEvent>> listenEvent() {
        return Arrays.asList(DropDatabaseEvent.class, CreateTableEvent.class, DropTableEvent.class);
    }
}
