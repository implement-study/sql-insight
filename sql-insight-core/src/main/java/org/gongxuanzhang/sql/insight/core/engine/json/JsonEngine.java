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
import org.gongxuanzhang.sql.insight.core.object.InsertRow;
import org.gongxuanzhang.sql.insight.core.object.Table;
import org.gongxuanzhang.sql.insight.core.result.ExceptionResult;
import org.gongxuanzhang.sql.insight.core.result.MessageResult;
import org.gongxuanzhang.sql.insight.core.result.ResultInterface;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
@Slf4j
public class JsonEngine implements StorageEngine {

    private final AutoIncrementKeyCounter counter = new AutoIncrementKeyCounter();

    @Override
    public String getName() {
        return "json";
    }

    @Override
    public List<String> tableExtensions() {
        return Arrays.asList("json");
    }

    @Override
    public ResultInterface createTable(Table table) {
        File dbFolder = table.getDatabase().getDbFolder();
        File jsonFile = new File(dbFolder, table.getName() + ".json");
        try {
            if (!jsonFile.createNewFile()) {
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
        byte[] bytes = jsonObject.toJSONBBytes();
        //  拿到对应行的id 插入位置
        return null;
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

    private JSONObject fullAllColumnRow(InsertRow row) {

        return null;
    }
}
