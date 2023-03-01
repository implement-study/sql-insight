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

package org.gongxuanzhang.mysql.core.select;

import org.gongxuanzhang.mysql.core.Available;

import java.util.Comparator;

/**
 * 排序字段
 * 用于辅助查询
 *
 * @author gongxuanzhang
 */
public interface Order<T> extends Comparator<T>, Available {


    /**
     * 添加排序列
     *
     * @param col       列名
     * @param orderEnum 排序方式
     **/
    void addOrder(String col, OrderEnum orderEnum);

    /**
     * 默认升序
     *
     * @param col 列名
     **/
    default void addOrder(String col) {
        addOrder(col, OrderEnum.asc);
    }
}
