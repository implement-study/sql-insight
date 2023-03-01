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

import org.gongxuanzhang.mysql.entity.DatabaseInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.tool.CollectionUtils;
import org.gongxuanzhang.mysql.tool.Context;

import java.io.File;

/**
 * 数据库管理
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class DatabaseManager extends AbstractManager<DatabaseInfo> {

    public DatabaseManager() throws MySQLException {
    }

    @Override
    protected String errorMessage() {
        return "数据库";
    }

    @Override
    protected void init() throws MySQLException {
        File home = Context.getHome();
        File[] databases = home.listFiles(File::isDirectory);
        CollectionUtils.foreachIfNotEmpty(databases, databaseFile -> {
            DatabaseInfo databaseInfo = new DatabaseInfo(databaseFile.getName());
            this.register(databaseInfo);
        });
    }

    @Override
    public String toId(DatabaseInfo databaseInfo) {
        return databaseInfo.getDatabaseName();
    }


}
