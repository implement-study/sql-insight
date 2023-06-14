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

import java.util.ArrayList;
import java.util.List;


/**
 * json排序
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class JsonOrder implements Order<JSONObject> {

    private final List<String> orderCol = new ArrayList<>();

    private final List<OrderEnum> enums = new ArrayList<>();

    @Override
    public int compare(JSONObject o1, JSONObject o2) {
        for (int i = 0; i < orderCol.size(); i++) {
            int compareValue = compareCol(o1, o2, i);
            if (compareValue != 0) {
                return compareValue;
            }
        }
        return 0;
    }

    private <T> int compareCol(JSONObject o1, JSONObject o2, int compareIndex) {
        String orderCol = this.orderCol.get(compareIndex);
        OrderEnum orderEnum = enums.get(compareIndex);
        Comparable<T> o1Value = o1.getObject(orderCol, Comparable.class);
        if (o1Value == null) {
            return orderEnum == OrderEnum.asc ? -1 : 1;
        }
        Comparable<T> o2Value = o2.getObject(orderCol, Comparable.class);
        if (o2Value == null) {
            return orderEnum == OrderEnum.asc ? 1 : -1;
        }
        return orderEnum == OrderEnum.asc ? o1Value.compareTo((T) o2Value) : o2Value.compareTo((T) o1Value);
    }

    @Override
    public boolean available() {
        return !orderCol.isEmpty();
    }

    @Override
    public void addOrder(String col, OrderEnum orderEnum) {
        orderCol.add(col);
        enums.add(orderEnum);
    }
}
