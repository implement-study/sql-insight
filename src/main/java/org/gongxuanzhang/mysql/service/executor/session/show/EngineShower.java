/*
 * Copyright 2023 java-mysql  and the original author or authors <gongxuanzhangmelt@gmail.com>.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/implement-study/java-mysql/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gongxuanzhang.mysql.service.executor.session.show;

import com.alibaba.fastjson2.JSONObject;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.storage.StorageEngine;
import org.gongxuanzhang.mysql.tool.Context;

import java.util.List;
import java.util.stream.Collectors;

/**
 * show engines
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class EngineShower implements Shower {


    private static final String[] HEAD = new String[]{"name", "transaction"};


    @Override
    public Result show() {
        List<JSONObject> data =
                Context.getEngineList()
                        .stream()
                        .map(this::engineToResult)
                        .collect(Collectors.toList());
        return Result.select(HEAD, data);
    }

    private JSONObject engineToResult(StorageEngine engine) {
        JSONObject jsonObject = new JSONObject(8);
        jsonObject.put(HEAD[0], engine.getEngineName());
        jsonObject.put(HEAD[1], Boolean.toString(engine.supportTransaction()));
        return jsonObject;
    }
}
