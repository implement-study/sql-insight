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

package org.gongxuanzhang.mysql.service.executor.dml;

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.TruncateInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.executor.ddl.BatchDdlExecutor;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * truncate执行器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class TruncateExecutor extends BatchDdlExecutor<TruncateInfo> {


    public TruncateExecutor(List<TruncateInfo> infos) {
        super(infos);
    }

    public Result doExecute(TruncateInfo info) throws MySQLException {
        if (info.getTableInfo().dataFile().delete()) {
            return Result.info("成功删除" + info.getTableInfo().getTableName() + "表");
        }
        return Result.error("删除表失败");
    }

    @Override
    public Result doExecute(List<TruncateInfo> infos) throws MySQLException {
        List<String> success = new ArrayList<>(infos.size());
        List<String> error = new ArrayList<>(infos.size());

        for (TruncateInfo info : infos) {
            if (info.getTableInfo().dataFile().delete()) {
                success.add(info.getTableInfo().getTableName());
            } else {
                error.add(info.getTableInfo().getTableName());
            }
        }
        if (CollectionUtils.isEmpty(error)) {
            return Result.info("成功删除" + String.join(",", success) + "表");
        }
        String errorMessage = String.format("删除%s失败", String.join(",", error));
        if (!CollectionUtils.isEmpty(success)) {
            errorMessage += String.format(" 删除%s成功", String.join(",", success));
        }
        return Result.error(errorMessage);
    }
}
