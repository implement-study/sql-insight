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

package org.gongxuanzhang.mysql.service.executor.session;

import org.gongxuanzhang.mysql.core.MySqlSession;
import org.gongxuanzhang.mysql.core.SessionManager;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.GlobalProperties;
import org.gongxuanzhang.mysql.entity.VariableInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.executor.Executor;

import static org.gongxuanzhang.mysql.tool.ExceptionThrower.errorSwap;

/**
 * set执行器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SetExecutor implements Executor {

    private final VariableInfo varInfo;

    public SetExecutor(VariableInfo setInfo) {
        this.varInfo = setInfo;
    }

    @Override
    public Result doExecute() throws MySQLException {
        try {
            if (varInfo.isGlobal()) {
                GlobalProperties.getInstance().set(varInfo.getName(), varInfo.getValue());
            } else {
                MySqlSession mySqlSession = SessionManager.currentSession();
                mySqlSession.set(varInfo.getName(), varInfo.getValue());
            }
            return Result.success();
        } catch (Exception e) {
            return errorSwap(e);
        }
    }
}
