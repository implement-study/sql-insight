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

import lombok.Data;

import java.util.List;

/**
 * 主键定义
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class PrimaryKeyDefinition {


    private boolean autoIncrement;

    /**
     * 主键是由多少个列组成的
     * 如果是独立主键，此值是1
     * 如果没有指定主键，此值是0
     **/
    private int colCount;

    /**
     * 列名
     * 如果没有指定主键，此属性是空集合
     **/
    private List<String> columnNames;


}
