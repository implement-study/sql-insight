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

package org.gongxuanzhang.mysql.storage.fool;

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.TruncateInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.storage.TruncateEngine;

/**
 * fool 引擎的删除表
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class FoolTruncate implements TruncateEngine {


    @Override
    public Result truncate(TruncateInfo info) throws MySQLException {
        if (info.getTableInfo().dataFile().delete()) {
            return Result.info("成功删除" + info.getTableInfo().getTableName() + "表");
        }
        return Result.error("删除表失败");
    }
}
