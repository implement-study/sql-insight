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

package org.gongxuanzhang.mysql.entity;

import com.alibaba.druid.sql.ast.statement.SQLCreateDatabaseStatement;
import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.mysql.annotation.DependOnContext;

/**
 * 创建数据库信息
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@DependOnContext
@Slf4j
public class CreateDatabaseInfo extends DatabaseInfo implements Ignorable {

    private boolean ifNotExists;

    public CreateDatabaseInfo(SQLCreateDatabaseStatement statement) {
        super(statement.getDatabaseName());
        setNotIfExists(statement.isIfNotExists());
    }


    @Override
    public void setNotIfExists(boolean notIfExists) {
        this.ifNotExists = notIfExists;
    }

    @Override
    public boolean notIfExists() {
        return ifNotExists;
    }
}