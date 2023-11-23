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
import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.sql.insight.core.engine.storage.StorageEngine;
import org.gongxuanzhang.sql.insight.core.environment.AutoIncrementKeyCounter;
import org.gongxuanzhang.sql.insight.core.exception.CreateTableException;
import org.gongxuanzhang.sql.insight.core.exception.InsertException;
import org.gongxuanzhang.sql.insight.core.exception.RuntimeIoException;
import org.gongxuanzhang.sql.insight.core.object.Column;
import org.gongxuanzhang.sql.insight.core.object.DataType;
import org.gongxuanzhang.sql.insight.core.object.InsertRow;
import org.gongxuanzhang.sql.insight.core.object.Table;
import org.gongxuanzhang.sql.insight.core.object.value.Value;
import org.gongxuanzhang.sql.insight.core.result.ExceptionResult;
import org.gongxuanzhang.sql.insight.core.result.InsertResult;
import org.gongxuanzhang.sql.insight.core.result.MessageResult;
import org.gongxuanzhang.sql.insight.core.result.ResultInterface;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
@Slf4j
public class JsonEngine implements StorageEngine {

    private final AutoIncrementKeyCounter counter = new AutoIncrementKeyCounter();

    private final int maxPrimaryKey = 10000;

    private final int minPrimaryKey = 1;

    @Override
    public String getName() {
        return "json";
    }

    @Override
    public List<String> tableExtensions() {
        return Collections.singletonList("json");
    }

    @Override
    public ResultInterface createTable(Table table) {
        File dbFolder = table.getDatabase().getDbFolder();
        if (table.getPrimaryKeyIndex() == -1) {
            throw new CreateTableException("engine json table must have a primary key");
        }
        if (table.getColumnList().get(table.getPrimaryKeyIndex()).getDataType().getType() != DataType.Type.INT) {
            throw new CreateTableException("engine json table primary key must int ");
        }
        File jsonFile = new File(dbFolder, table.getName() + ".json");
        try {
            if (!jsonFile.createNewFile()) {
                List<String> initContent = new ArrayList<>();
                for (int i = 0; i < maxPrimaryKey; i++) {
                    initContent.add("");
                }
                Files.write(jsonFile.toPath(), initContent);
                log.warn("create file {} fail", jsonFile.getName());
            }
            return new MessageResult(String.format("成功创建%s", table.getName()));
        } catch (IOException e) {
            return new ExceptionResult(e);
        }
    }

    @Override
    public ResultInterface truncateTable(Table table) {
        return null;
    }

    @Override
    public ResultInterface insert(InsertRow row) {
        //   todo lock
        counter.dealAutoIncrement(row);
        JSONObject jsonObject = fullAllColumnRow(row);
        File jsonFile = getJsonFile(row.getTable());
        int insertPrimaryKey = getJsonInsertRowPrimaryKey(row, jsonObject);
        if (maxPrimaryKey < insertPrimaryKey || insertPrimaryKey < minPrimaryKey) {
            throw new InsertException("engine json primary key must between " + minPrimaryKey + " and " + maxPrimaryKey);
        }
        try {
            List<String> lines = Files.readAllLines(jsonFile.toPath());
            String currentLine = lines.get(insertPrimaryKey);
            if (currentLine.isEmpty()) {
                throw new InsertException(String.format("Duplicate entry '%s' for key 'PRIMARY'", insertPrimaryKey));
            }
            lines.set(insertPrimaryKey, jsonObject.toString());
            Files.write(jsonFile.toPath(), lines);
        } catch (IOException e) {
            throw new RuntimeIoException(e);
        }
        getJsonInsertRowPrimaryKey(row, jsonObject);
        //  拿到对应行的id 插入位置
        return new InsertResult(1);
    }


    @Override
    public ResultInterface update() {
        return null;
    }

    @Override
    public ResultInterface delete() {
        return null;
    }

    @Override
    public ResultInterface query() {
        return null;
    }

    private File getJsonFile(Table table) {
        return new File(table.getDatabase().getDbFolder(), table.getName() + ".json");
    }

    private JSONObject fullAllColumnRow(InsertRow row) {
        Table table = row.getTable();
        JSONObject jsonObject = new JSONObject();
        table.getColumnList().forEach(col -> jsonObject.put(col.getName(), null));
        List<Column> insertColumns = row.getInsertColumns();
        List<Value> values = row.getValues();
        for (int i = 0; i < values.size(); i++) {
            jsonObject.put(insertColumns.get(i).getName(), values.get(i).getSource());
        }
        return jsonObject;
    }

    private int getJsonInsertRowPrimaryKey(InsertRow row, JSONObject json) {
        Table table = row.getTable();
        List<Column> columnList = table.getColumnList();
        Column column = columnList.get(table.getPrimaryKeyIndex());
        return json.getInteger(column.getName());
    }


}
