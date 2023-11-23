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
import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.sql.insight.core.object.InsertRow;
import org.gongxuanzhang.sql.insight.core.object.value.AutoIncrementValue;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
@Slf4j
public class AutoIncrementKeyCounter {


    private final Table<String, String, AtomicLong> keyTable = Tables.synchronizedTable(HashBasedTable.create());

    /**
     * if auto increment column have value,fresh cache value.
     * else set a value to row
     *
     * @param row insert row
     **/
    public void dealAutoIncrement(InsertRow row) {
        int autoColIndex = row.getTable().getAutoColIndex();
        if (autoColIndex < 0) {
            return;
        }
        String databaseName = row.getTable().getDatabase().getName();
        AtomicLong atomicLong = loadMaxAutoIncrementKey(databaseName, row.getTable().getName());

        AutoIncrementValue autoCol = (AutoIncrementValue) row.getValues().get(autoColIndex);
        if (autoCol.getSource() == null) {
            autoCol.setValue((int) atomicLong.incrementAndGet());
            return;
        }
        if (autoCol.getSource() > atomicLong.get()) {
            log.info("database[{}],table[{}],auto increment col value set {}", databaseName, row.getTable().getName()
                    , autoCol.getSource());
            atomicLong.set(autoCol.getSource());
        }
    }


    private AtomicLong loadMaxAutoIncrementKey(String database, String tableName) {
        //  todo load from disk
        AtomicLong atomicLong = keyTable.get(database, tableName);
        if (atomicLong == null) {
            synchronized (keyTable) {
                if (keyTable.get(database, tableName) == null) {
                    atomicLong = new AtomicLong();
                    return keyTable.put(database, tableName, atomicLong);
                }
            }
        }
        return atomicLong;
    }


}
