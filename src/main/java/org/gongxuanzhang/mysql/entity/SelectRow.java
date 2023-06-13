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

import java.util.Map;

/**
 * 查询行
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface SelectRow extends Row {


    /**
     * 转换成查询Map
     * 比如 select * from table1
     * 有一行数据 id:1 name:Tom
     * Map:
     * key id
     * value 1
     * key name
     * value Tom
     *
     * @return Map
     **/
    Map<String, String> showMap();
}
