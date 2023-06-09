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

package org.gongxuanzhang.mysql.service.executor;

import org.gongxuanzhang.mysql.entity.ExecuteInfo;
import org.gongxuanzhang.mysql.storage.StorageEngine;

/**
 * 和存储引擎相关的执行器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public abstract class EngineExecutor<T extends ExecuteInfo> implements Executor {

    private final T info;

    public EngineExecutor(T info) {
        this.info = info;
    }


    /**
     * 返回此执行器的执行引擎
     *
     * @return 返回个啥
     **/
    public abstract StorageEngine getEngine();


    public T getInfo() {
        return info;
    }


}
