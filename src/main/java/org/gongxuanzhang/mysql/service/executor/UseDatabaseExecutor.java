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

import org.gongxuanzhang.mysql.core.MySqlSession;
import org.gongxuanzhang.mysql.core.SessionManager;
import org.gongxuanzhang.mysql.core.manager.DatabaseManager;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.DatabaseInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.tool.Context;

/**
 * 切换数据库
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class UseDatabaseExecutor implements Executor {

    private final String database;

    public UseDatabaseExecutor(String database) {
        this.database = database;
    }

    @Override
    public Result doExecute() throws MySQLException {
        MySqlSession mySqlSession = SessionManager.currentSession();
        DatabaseManager databaseManager = Context.getDatabaseManager();
        DatabaseInfo select = databaseManager.select(this.database);
        if (select == null) {
            return Result.error("数据库" + database + "不存在");
        }
        if (mySqlSession.useDatabase(select)) {
            Result.info("切换数据库成功");
        }
        return Result.info("没有改变当前数据库" + mySqlSession.getDatabase().getDatabaseName());
    }
}
