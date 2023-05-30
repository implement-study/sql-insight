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

import org.gongxuanzhang.mysql.annotation.InitAfter;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.storage.StorageEngine;

/**
 * 引擎管理
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@InitAfter(DatabaseManager.class)
public class EngineManager extends AbstractManager<StorageEngine> {


    public EngineManager() throws MySQLException {
    }

    @Override
    protected void init() throws MySQLException {

    }

    @Override
    protected String errorMessage() {
        return "引擎";
    }

    @Override
    public String toName(StorageEngine engine) {
        return engine.getEngineName();
    }
}
