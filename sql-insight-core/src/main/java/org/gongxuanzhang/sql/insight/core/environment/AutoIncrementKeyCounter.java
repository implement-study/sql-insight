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
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import org.gongxuanzhang.sql.insight.core.event.BeforeInsertEvent;
import org.gongxuanzhang.sql.insight.core.event.DropDatabaseEvent;
import org.gongxuanzhang.sql.insight.core.event.DropTableEvent;
import org.gongxuanzhang.sql.insight.core.event.InsightEvent;
import org.gongxuanzhang.sql.insight.core.event.MultipleEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class AutoIncrementKeyCounter implements MultipleEventListener {


    private final Table<String, String, AtomicLong> keyTable = Tables.synchronizedTable(HashBasedTable.create());


    @Override
    public void onEvent(InsightEvent event) {
        if (event instanceof DropDatabaseEvent) {
            keyTable.row(((DropDatabaseEvent) event).getDatabase().getName()).clear();
            return;
        }
        if (event instanceof DropTableEvent) {
            DropTableEvent dropTableEvent = (DropTableEvent) event;
            keyTable.row(dropTableEvent.getDatabaseName()).remove(dropTableEvent.getTableName());
        }
        if(event instanceof BeforeInsertEvent){
            //  todo  count update
        }
    }

    @Override
    public List<Class<? extends InsightEvent>> listenEvent() {
        return Arrays.asList(DropDatabaseEvent.class, DropTableEvent.class, BeforeInsertEvent.class);
    }


}
