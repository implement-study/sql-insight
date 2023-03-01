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

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.exception.MySQLException;

/**
 * 执行器，一般是解析Sql得到的
 *
 * @author gxz gongxuanzhang@foxmail.com
 * @see org.gongxuanzhang.mysql.service.analysis.TokenAnalysis
 **/
public interface Executor {

    /**
     * 执行
     *
     * @return 返回执行结果
     * @throws MySQLException 执行过程中出现问题抛出异常
     **/
    Result doExecute() throws MySQLException;
}
