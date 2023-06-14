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

package org.gongxuanzhang.mysql.service.executor;

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.ExecuteInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;

import java.util.List;

/**
 * 支持批量执行
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
public abstract class BatchExecutor<T extends ExecuteInfo> implements Executor {

    protected final List<T> infos;

    public BatchExecutor(List<T> infos) {
        this.infos = infos;
    }

    public List<T> getInfos() {
        return infos;
    }


    /**
     * 批量执行信息
     *
     * @param infos 批量信息
     * @return {@link this#doExecute()}
     * @throws MySQLException 执行异常
     **/
    public abstract Result doExecute(List<T> infos) throws MySQLException;


    @Override
    public Result doExecute() throws MySQLException {
        return doExecute(this.getInfos());
    }
}
