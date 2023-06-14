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

import org.jetbrains.annotations.NotNull;

/**
 * 主键
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface PrimaryKey extends ExecuteInfo, Comparable<PrimaryKey> {


    /**
     * 主键是可以比较的
     *
     * @param other 同 compare
     * @return 同compare to
     **/
    @Override
    int compareTo(@NotNull PrimaryKey other);


    /**
     * 主键长度
     *
     * @return 如果是动态长度主键返回具体长度
     **/
    int length();


    /**
     * 是否是动态主键
     *
     * @return true 是动态主键
     **/
    boolean isDynamic();


}
