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

package org.gongxuanzhang.mysql.entity;

import org.gongxuanzhang.mysql.core.TableInfoBox;

import java.util.List;

/**
 * 一行数据
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface Row extends ExecuteInfo, TableInfoBox {


    /**
     * 一行数据，内容装着各个cell
     *
     * @return 一行数据
     **/
    List<Cell<?>> getCellList();

    /**
     * 表信息
     *
     * @return 不能为null
     **/
    @Override
    TableInfo getTableInfo();

    /**
     * 设置表信息
     *
     * @param tableInfo 不会为null
     **/
    @Override
    default void setTableInfo(TableInfo tableInfo) {
        throw new UnsupportedOperationException("不支持手动设置tableInfo");
    }
}
