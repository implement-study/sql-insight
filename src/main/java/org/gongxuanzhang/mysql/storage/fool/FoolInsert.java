/*
 * Copyright 2023 sql-insight  and the original author or authors <gongxuanzhangmelt@gmail.com>.
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

package org.gongxuanzhang.mysql.storage.fool;

import com.alibaba.fastjson2.JSONObject;
import org.gongxuanzhang.mysql.core.ColumnAdjust;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.InsertData;
import org.gongxuanzhang.mysql.entity.InsertInfo;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.storage.InsertEngine;
import org.gongxuanzhang.mysql.tool.FileUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * fool 引擎的插入模板
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class FoolInsert implements InsertEngine {

    @Override
    public Result insert(InsertInfo info) throws MySQLException {
        ColumnAdjust columnAdjust = new ColumnAdjust(info);
        InsertData insertData = columnAdjust.adjust();
        TableInfo tableInfo = info.getTableInfo();
        Set<String> uniqueKeys = tableInfo.uniqueKeys();
        if (uniqueKeys.isEmpty()) {
            return withoutUnique(insertData, tableInfo.dataFile().toPath());
        }
        return uniqueData(insertData, tableInfo);
    }


    private Result uniqueData(InsertData insertData, TableInfo tableInfo) throws MySQLException {
        Set<String> uniqueKeys = tableInfo.uniqueKeys();
        FileUtils.readAllLines(tableInfo.dataFile().toPath(), (line) -> {
            JSONObject jsonObject = JSONObject.parseObject(line);
            for (String uniqueKey : uniqueKeys) {
                Object uniqueValue = jsonObject.get(uniqueKey);
                if (uniqueValue != null && insertData.containsUnique(uniqueKey, uniqueValue)) {
                    throw new MySQLException(String.format("键[%s],值[%s]已经存在", uniqueKey, uniqueValue));
                }
            }
        });
        FileUtils.append(tableInfo.dataFile().toPath(), insertData.getInsertStr());
        if (tableInfo.getIncrementKey() != null) {
            tableInfo.refresh();
        }
        return Result.info(String.format("成功插入%s条数据", insertData.getInsertStr().size()));
    }

    private Result withoutUnique(InsertData insertData, Path dataPath) throws MySQLException {
        List<String> insertStr = insertData.getData().stream().map(JSONObject::toString).collect(Collectors.toList());
        FileUtils.append(dataPath, insertStr);
        return Result.info(String.format("成功插入%s条数据", insertStr.size()));
    }
}
