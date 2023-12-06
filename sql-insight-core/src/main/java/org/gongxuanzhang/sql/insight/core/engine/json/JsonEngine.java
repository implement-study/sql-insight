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
import org.gongxuanzhang.sql.insight.core.command.dml.Delete;
import org.gongxuanzhang.sql.insight.core.command.dml.Update;
import org.gongxuanzhang.sql.insight.core.engine.storage.StorageEngine;
import org.gongxuanzhang.sql.insight.core.exception.CreateTableException;
import org.gongxuanzhang.sql.insight.core.exception.InsertException;
import org.gongxuanzhang.sql.insight.core.exception.RuntimeIoException;
import org.gongxuanzhang.sql.insight.core.object.Column;
import org.gongxuanzhang.sql.insight.core.object.DataType;
import org.gongxuanzhang.sql.insight.core.object.InsertRow;
import org.gongxuanzhang.sql.insight.core.object.PhysicRow;
import org.gongxuanzhang.sql.insight.core.object.Row;
import org.gongxuanzhang.sql.insight.core.object.Table;
import org.gongxuanzhang.sql.insight.core.object.Where;
import org.gongxuanzhang.sql.insight.core.object.value.Value;
import org.gongxuanzhang.sql.insight.core.result.DeleteResult;
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

    private final JsonIncrementKeyCounter counter = new JsonIncrementKeyCounter();

    private static final int MAX_PRIMARY_KEY = 10000;

    private static final int MIN_PRIMARY_KEY = 1;

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
                log.warn("create file {} fail", jsonFile.getName());
            }
            List<String> initContent = new ArrayList<>();
            for (int i = 0; i < MAX_PRIMARY_KEY; i++) {
                initContent.add("");
            }
            Files.write(jsonFile.toPath(), initContent);
            log.info("write {} json to {}", initContent.size(), jsonFile.toPath().toAbsolutePath());
            return new MessageResult(String.format("成功创建%s", table.getName()));
        } catch (IOException e) {
            return new ExceptionResult(e);
        }
    }

    @Override
    public ResultInterface truncateTable(Table table) {
        File jsonFile = JsonEngineSupport.getJsonFile(table);
        List<String> initContent = new ArrayList<>();
        for (int i = 0; i < MAX_PRIMARY_KEY; i++) {
            initContent.add("");
        }
        try {
            Files.write(jsonFile.toPath(), initContent);
            counter.reset(table);
            return new MessageResult();
        } catch (IOException e) {
            return new ExceptionResult(e);
        }
    }

    @Override
    public ResultInterface insertRow(InsertRow row) {
        counter.dealAutoIncrement(row);
        JSONObject jsonObject = fullAllColumnRow(row);
        File jsonFile = JsonEngineSupport.getJsonFile(row.getTable());
        int insertPrimaryKey = JsonEngineSupport.getJsonInsertRowPrimaryKey(row.getTable(), jsonObject);
        if (MAX_PRIMARY_KEY < insertPrimaryKey || insertPrimaryKey < MIN_PRIMARY_KEY) {
            throw new InsertException("engine json primary key must between " + MIN_PRIMARY_KEY + " and " + MAX_PRIMARY_KEY);
        }
        try {
            List<String> lines = Files.readAllLines(jsonFile.toPath());
            String currentLine = lines.get(insertPrimaryKey);
            if (!currentLine.isEmpty()) {
                throw new InsertException(String.format("Duplicate entry '%s' for key 'PRIMARY'", insertPrimaryKey));
            }
            lines.set(insertPrimaryKey, jsonObject.toString());
            log.info("insert {} to table [{}] ", jsonObject, row.getTable().getName());
            Files.write(jsonFile.toPath(), lines);
        } catch (IOException e) {
            throw new RuntimeIoException(e);
        }
        //  拿到对应行的id 插入位置
        return new InsertResult(1);
    }

    @Override
    public ResultInterface update(Row oldRow, Update update) {
        return null;
    }

    @Override
    public ResultInterface delete(Row deletedRow) {
        return null;
    }


    public ResultInterface update(Update update) {
        File jsonFile = JsonEngineSupport.getJsonFile(update.getTable());
        int updateCount = 0;
        try {
            List<String> lines = Files.readAllLines(jsonFile.toPath());
            Where where = update.getWhere();
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isEmpty()) {
                    continue;
                }
                final int lineNumber = i;
                JSONObject jsonObject = JSONObject.parseObject(line);
                PhysicRow row = JsonEngineSupport.getPhysicRowFromJson(jsonObject, update.getTable());
                if (Boolean.TRUE.equals(where.getBooleanValue(row))) {
                    update.getUpdateField().forEach((colName, expression) -> {
                        Value expressionValue = expression.getExpressionValue(row);
                        jsonObject.put(colName, expressionValue.getSource());
                        String newLine = jsonObject.toString();
                        lines.set(lineNumber, newLine);
                        log.info("update {} to {} ", line, newLine);
                    });
                    updateCount++;
                }
            }
            if (updateCount > 0) {
                Files.write(jsonFile.toPath(), lines);
            }
        } catch (IOException e) {
            throw new RuntimeIoException(e);
        }
        return new DeleteResult(updateCount);
    }

    public ResultInterface delete(Delete delete) {
        File jsonFile = JsonEngineSupport.getJsonFile(delete.getTable());
        int deleteCount = 0;
        try {
            List<String> lines = Files.readAllLines(jsonFile.toPath());
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isEmpty()) {
                    continue;
                }
                PhysicRow row = JsonEngineSupport.getPhysicRowFromJson(JSONObject.parseObject(line), delete.getTable());
                if (Boolean.TRUE.equals(delete.getWhere().getBooleanValue(row))) {
                    log.info("delete row {} ", line);
                    lines.set(i, "");
                    deleteCount++;
                }
            }
            if (deleteCount > 0) {
                Files.write(jsonFile.toPath(), lines);
            }
        } catch (IOException e) {
            throw new RuntimeIoException(e);
        }
        return new DeleteResult(deleteCount);
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


}
