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
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Tables;
import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.sql.insight.core.engine.AutoIncrementKeyCounter;
import org.gongxuanzhang.sql.insight.core.exception.RuntimeIoException;
import org.gongxuanzhang.sql.insight.core.object.InsertRow;
import org.gongxuanzhang.sql.insight.core.object.Table;
import org.gongxuanzhang.sql.insight.core.object.value.Value;
import org.gongxuanzhang.sql.insight.core.object.value.ValueInt;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
@Slf4j
public class JsonIncrementKeyCounter implements AutoIncrementKeyCounter {


    private final com.google.common.collect.Table<String, String, AtomicLong> keyTable =
            Tables.synchronizedTable(HashBasedTable.create());

    /**
     * if auto increment column have value,fresh cache value.
     * else set a value to row
     *
     * @param row insert row
     **/
    @Override
    public void dealAutoIncrement(InsertRow row) {
        int autoColIndex = row.getTable().getAutoColIndex();
        if (autoColIndex < 0) {
            return;
        }
        String databaseName = row.getTable().getDatabase().getName();
        AtomicLong atomicLong = loadMaxAutoIncrementKey(row.getTable());

        Value autoIncrementValue = row.getValues().get(autoColIndex);

        if (autoIncrementValue.getSource() == null) {
            row.getValues().set(autoColIndex, new ValueInt((int) atomicLong.incrementAndGet()));
            return;
        }
        int insertValue = (int) autoIncrementValue.getSource();
        if (insertValue > atomicLong.get()) {
            log.info("database[{}],table[{}],auto increment col value set {}", databaseName, row.getTable().getName()
                    , insertValue);
            atomicLong.set(insertValue);
        }
    }

    @Override
    public void reset(Table table) {
        AtomicLong atomicLong = loadMaxAutoIncrementKey(table);
        atomicLong.set(0);
    }

    private AtomicLong loadMaxAutoIncrementKey(Table table) {
        String database = table.getDatabase().getName();
        String tableName = table.getName();
        AtomicLong atomicLong = keyTable.get(database, tableName);
        if (atomicLong == null) {
            synchronized (keyTable) {
                if (keyTable.get(database, tableName) == null) {
                    atomicLong = loadFromDisk(table);
                    keyTable.put(database, tableName, atomicLong);
                }
            }
        }
        return Objects.requireNonNull(atomicLong);
    }

    private AtomicLong loadFromDisk(Table table) {
        File jsonFile = JsonEngineSupport.getJsonFile(table);
        try {
            List<String> allLines = Files.readAllLines(jsonFile.toPath());
            for (int i = allLines.size() - 1; i >= 0; i--) {
                if (!allLines.get(i).isEmpty()) {
                    int key = JsonEngineSupport.getJsonInsertRowPrimaryKey(table, JSONObject.parse(allLines.get(i)));
                    return new AtomicLong(key);
                }
            }
            return new AtomicLong();
        } catch (IOException e) {
            throw new RuntimeIoException(e);
        }
    }


}
