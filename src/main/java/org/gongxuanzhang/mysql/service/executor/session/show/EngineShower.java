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

package org.gongxuanzhang.mysql.service.executor.session.show;

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.storage.StorageEngine;
import org.gongxuanzhang.mysql.tool.Context;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * show engines
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class EngineShower implements Shower {


    private static final List<String> HEAD = new ArrayList<>();

    static {
        HEAD.add("name");
        HEAD.add("transaction");
    }

    @Override
    public Result show() {
        List<Map<String, String>> data =
                Context.getEngineList()
                        .stream()
                        .map(this::engineToResult)
                        .collect(Collectors.toList());
        return Result.select(HEAD, data);
    }

    private Map<String, String> engineToResult(StorageEngine engine) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(HEAD.get(0), engine.getEngineName());
        map.put(HEAD.get(1), Boolean.toString(engine.supportTransaction()));
        return map;
    }
}
