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

package org.gongxuanzhang.mysql.service.executor.ddl;

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.ExecuteInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.executor.AbstractExecutor;

/**
 * ddl执行器
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
public abstract class DdlExecutor<T extends ExecuteInfo> extends AbstractExecutor<T> {

    public DdlExecutor(T sqlStatement) {
        super(sqlStatement);
    }


    /**
     * 用信息执行
     *
     * @param info 信息
     * @return 同 {@link this#doExecute()}
     * @throws MySQLException 异常信息
     **/
    public abstract Result doExecute(T info) throws MySQLException;


    @Override
    public Result doExecute() throws MySQLException {
        return this.doExecute(this.getInfo());
    }
}
