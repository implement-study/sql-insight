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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理器的骨架实现
 * 用一个 concurrentHashMap管理
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public abstract class AbstractManager<T> implements MySQLManager<T> {


    private final Map<String, T> cache = new ConcurrentHashMap<>();

    public AbstractManager() throws MySQLException {
    }

    @Override
    public void register(T t) {
        cache.put(toName(t), t);
    }

    @Override
    public T select(String name) throws MySQLException {
        T t = cache.get(name);
        if (t == null) {
            throw new MySQLException(String.format("不存在%s的[%s]", name, errorMessage()));
        }
        return t;
    }


    @Override
    public void remove(String name) {
        cache.remove(name);
    }

    /**
     * 找不到内容的异常信息
     *
     * @return 异常信息 not null
     */
    protected abstract String errorMessage();


    @Override
    public List<T> getAll() {
        return new ArrayList<>(cache.values());
    }


    @Override
    public void refresh() throws MySQLException {
        cache.clear();
        init();
    }

    /**
     * 拿到缓存
     *
     * @return not null
     **/
    protected Map<String, T> getCache() {
        return this.cache;
    }


    /**
     * 初始化内容，在构造和refresh的时候会触发
     *
     * @throws MySQLException 初始化发生异常触发
     **/
    protected abstract void init() throws MySQLException;


}
