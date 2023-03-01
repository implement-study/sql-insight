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


import lombok.Data;

/**
 * 查询列内容
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class SelectCol {
    /**
     * 原列名
     **/
    private final String colName;
    /**
     * 列别名
     **/
    private final String alias;
    /**
     * 从'*'查询
     **/
    private final boolean all;

    private SelectCol(String colName, String alias, boolean all) {
        this.colName = colName;
        this.alias = alias;
        this.all = all;
    }

    public static SelectCol allCol() {
        return new SelectCol(null, null, true);
    }

    public static SelectCol single(String colName, String alias) {
        return new SelectCol(colName, alias, false);
    }

}
