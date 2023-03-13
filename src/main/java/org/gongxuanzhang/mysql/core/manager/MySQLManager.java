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

package org.gongxuanzhang.mysql.core.manager;

import org.gongxuanzhang.mysql.exception.MySQLException;

import java.util.List;

/**
 * 管理器，负责管理数据。
 * 用于频繁使用的元数据减少io操作
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface MySQLManager<T> {

    /**
     * 注册信息
     *
     * @param t 管理的内容
     **/
    void register(T t);

    /**
     * 通过唯一表示获得管理的信息
     *
     * @param name 信息标识
     *
     * @return null 或者是信息
     **/
    T select(String name) throws MySQLException;

    default T select(T t) throws MySQLException {
        return select(toId(t));
    }

    /**
     * 删除内容
     *
     * @param name 标识
     **/
    void remove(String name);


    /**
     * 拿到所有的管理数据
     *
     * @return 如果没有数据返回空集合 不要返回null
     **/
    List<T> getAll();


    /**
     * 当内容有变动时刷新
     **/
    void refresh() throws MySQLException;

    /**
     * 被注册的内容如何变成唯一标识
     *
     * @param t 被注册的内容
     *
     * @return id
     **/
    String toId(T t);

}
