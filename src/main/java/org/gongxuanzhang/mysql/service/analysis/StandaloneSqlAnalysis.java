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

package org.gongxuanzhang.mysql.service.analysis;

import com.alibaba.druid.sql.ast.SQLStatement;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.executor.Executor;

/**
 * 只支持一种sql类型的解析器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface StandaloneSqlAnalysis {


    /**
     * 支持的sql类型
     *
     * @return 返回支持的sql类型
     **/
    Class<? extends SQLStatement> support();

    /**
     * 解析sql状态返回执行器
     *
     * @param sqlStatement sql
     *
     * @return 返回执行器
     *
     * @throws MySQLException 执行过程中出现问题
     **/
    Executor doAnalysis(SQLStatement sqlStatement) throws MySQLException;


}
