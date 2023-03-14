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

/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
public abstract class AbstractExecutor<T extends ExecuteInfo> implements Executor {

    protected T info;

    public AbstractExecutor(T info) {
        this.info = info;
    }


    public T getInfo() {
        return info;
    }


    /**
     * 根据信息执行
     *
     * @param info 执行信息
     * @return {@link this#doExecute()}
     * @throws MySQLException 执行异常
     **/
    public abstract Result doExecute(T info) throws MySQLException;


    @Override
    public Result doExecute() throws MySQLException {
        return this.doExecute(info);
    }
}
