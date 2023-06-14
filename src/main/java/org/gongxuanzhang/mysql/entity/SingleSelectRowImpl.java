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

package org.gongxuanzhang.mysql.entity;

import java.util.List;

/**
 * 单表查询行
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SingleSelectRowImpl implements Row {

    private final List<Cell<?>> cellList;

    private final TableInfo tableInfo;

    public SingleSelectRowImpl(List<Cell<?>> cellList) {
        this(cellList, null);
    }

    public SingleSelectRowImpl(List<Cell<?>> cellList, TableInfo tableInfo) {
        this.cellList = cellList;
        this.tableInfo = tableInfo;
    }


    @Override
    public List<Cell<?>> getCellList() {
        return this.cellList;
    }

    @Override
    public TableInfo getTableInfo() {
        return this.tableInfo;
    }


}
