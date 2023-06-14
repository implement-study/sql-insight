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

package org.gongxuanzhang.mysql.core.select;

import com.alibaba.fastjson2.JSONObject;
import org.gongxuanzhang.mysql.core.Available;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询条件
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class Where implements Available {

    private final List<Condition> andConditions = new ArrayList<>();

    private final List<Condition> orConditions = new ArrayList<>();

    public void addCondition(Condition condition) {
        if (condition.isAnd()) {
            andConditions.add(condition);
        } else {
            orConditions.add(condition);
        }
    }


    public boolean hit(JSONObject jsonObject) {
        for (Condition orCondition : orConditions) {
            if (orCondition.getValue(jsonObject)) {
                return true;
            }
        }
        for (Condition andCondition : andConditions) {
            if (!andCondition.getValue(jsonObject)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean available() {
        if (this.andConditions.isEmpty() && this.orConditions.isEmpty()) {
            return false;
        }
        return true;
    }
}
