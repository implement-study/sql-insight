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
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
public abstract class StorageEngineExecutor<T extends ExecuteInfo> extends AbstractExecutor<T> {

    private final StorageEngine engine;

    public StorageEngineExecutor(StorageEngine engine, T sqlStatement) {
        super(sqlStatement);
        this.engine = engine;
    }

    public StorageEngine getEngine() {
        return this.engine;
    }


    /**
     * 执行
     *
     * @param engine       引擎
     * @param sqlStatement sql执行信息
     *
     * @return 同Result
     *
     * @throws MySQLException 执行过程中可能出现的问题
     **/
    public abstract Result doExecute(StorageEngine engine, T sqlStatement) throws MySQLException;


    @Override
    public Result doExecute() throws MySQLException {
        return this.doExecute(this.engine, this.info);
    }
}
