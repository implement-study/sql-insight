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

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.ExecuteInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.storage.StorageEngine;

/**
 * 和存储引擎相关的执行器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public abstract class EngineExecutor<T extends ExecuteInfo> implements Executor {

    private final StorageEngine engine;
    private final T info;

    public EngineExecutor(StorageEngine engine, T info) {
        this.engine = engine;
        this.info = info;
    }


    /**
     * 引擎执行
     *
     * @param info   执行信息
     * @param engine 执行引擎
     * @return 统一返回值
     * @throws MySQLException 执行过程出现问题
     **/
    public abstract Result doEngine(StorageEngine engine, T info) throws MySQLException;


    @Override
    public Result doExecute() throws MySQLException {
        return this.doEngine(engine, info);
    }


    public StorageEngine getEngine() {
        return engine;
    }

    public T getInfo() {
        return info;
    }
}
