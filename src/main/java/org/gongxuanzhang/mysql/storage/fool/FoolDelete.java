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
import org.gongxuanzhang.mysql.core.select.SingleFrom;
import org.gongxuanzhang.mysql.core.select.Where;
import org.gongxuanzhang.mysql.entity.DeleteInfo;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.storage.DeleteEngine;
import org.gongxuanzhang.mysql.tool.ExceptionThrower;
import org.gongxuanzhang.mysql.tool.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * fool 引擎的删除操作
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class FoolDelete implements DeleteEngine {

    @Override
    public Result delete(DeleteInfo info) throws MySQLException {
        SingleFrom from = info.getFrom();
        TableInfo main = from.getMain();
        Where where = info.getWhere();
        File file = main.dataFile();
        List<String> remain = new ArrayList<>();
        int allCount = FileUtils.readAllLines(file.toPath(), (line) -> {
            JSONObject jsonObject = JSONObject.parseObject(line);
            if (!where.hit(jsonObject)) {
                remain.add(jsonObject.toString());
            }
        });
        try {
            Files.write(file.toPath(), remain);
            return Result.info(String.format("成功删除%s条记录", allCount - remain.size()));
        } catch (IOException e) {
            return ExceptionThrower.errorSwap(e);
        }
    }

}
