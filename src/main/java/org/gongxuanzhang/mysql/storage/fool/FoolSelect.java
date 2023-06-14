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
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.core.select.Order;
import org.gongxuanzhang.mysql.core.select.SelectCol;
import org.gongxuanzhang.mysql.core.select.Where;
import org.gongxuanzhang.mysql.entity.SingleSelectInfo;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.storage.SelectEngine;
import org.gongxuanzhang.mysql.tool.FileUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * fool 的 查询引擎
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class FoolSelect implements SelectEngine {


    @Override
    public Result select(SingleSelectInfo info) throws MySQLException {
        TableInfo tableInfo = info.getFrom();
        Where where = info.getWhere();
        List<SelectCol> selectCols = tableInfo.scatterCol(info.getAs());
        List<Map<String, String>> viewJson = new ArrayList<>();

        FileUtils.readAllLines(tableInfo.dataFile().toPath(), (line) -> {
            JSONObject jsonObject = JSONObject.parseObject(line);
            // where
            if (where.available() && !where.hit(jsonObject)) {
                return;
            }
            Map<String, String> map = new LinkedHashMap<>();
            jsonObject.forEach((k, v) -> map.put(k, v.toString()));
            viewJson.add(map);
        });
        //  as
        viewJson.forEach((json) -> {
            for (SelectCol selectCol : selectCols) {
                json.put(selectCol.getAlias(), json.get(selectCol.getColName()));
            }
        });
        // order
        Order<?> order = info.getOrder();
        if (order.available()) {
            viewJson.sort((Order<Map<String,String>>) order);
        }
        List<String> colNames = new ArrayList<>();
        for (SelectCol selectCol : selectCols) {
            colNames.add(selectCol.getAlias());
        }
        return Result.select(colNames, viewJson);
    }
}
