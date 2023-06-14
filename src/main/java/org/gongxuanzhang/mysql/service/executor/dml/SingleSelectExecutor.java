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

package org.gongxuanzhang.mysql.service.executor.dml;

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.SingleSelectInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.executor.EngineExecutor;
import org.gongxuanzhang.mysql.storage.StorageEngine;
import org.gongxuanzhang.mysql.tool.Context;

/**
 * 单表查询
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SingleSelectExecutor extends EngineExecutor<SingleSelectInfo> {

    private final StorageEngine storageEngine;

    public SingleSelectExecutor(SingleSelectInfo info) throws MySQLException {
        super(info);
        this.storageEngine = Context.selectStorageEngine(info.getFrom());
    }


    @Override
    public StorageEngine getEngine() {
        return storageEngine;
    }

    @Override
    public Result doExecute() throws MySQLException {
        return this.storageEngine.select(this.getInfo());
    }
}
