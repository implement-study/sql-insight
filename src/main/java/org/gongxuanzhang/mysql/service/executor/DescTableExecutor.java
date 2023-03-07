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

import org.gongxuanzhang.mysql.core.manager.TableManager;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.ExecuteException;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.executor.dcl.DclExecutor;
import org.gongxuanzhang.mysql.tool.Context;

/**
 * 展示表结构
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class DescTableExecutor extends DclExecutor<TableInfo> {

    private static final String[] TABLE_DESC_HEAD = new String[]{
            "field", "type", "notNull", "primary key", "default", "auto_increment", "unique", "comment"};


    public DescTableExecutor(TableInfo info) {
        super(info);
    }

    @Override
    public Result doExecute(TableInfo info) throws MySQLException {
        TableManager tableManager = Context.getTableManager();
        TableInfo select = tableManager.select(info.absoluteName());
        if (select == null) {
            throw new ExecuteException(String.format("表%s不存在", info.getTableName()));
        }
        return Result.select(TABLE_DESC_HEAD, select.descTable());
    }

}
