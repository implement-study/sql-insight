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

package org.gongxuanzhang.mysql

import com.alibaba.druid.sql.SQLUtils
import org.gongxuanzhang.mysql.core.CoreMySqlEngine
import org.gongxuanzhang.mysql.core.result.Result
import org.gongxuanzhang.mysql.service.analysis.SqlAnalysis
import org.gongxuanzhang.mysql.service.analysis.ast.SubSqlAnalysis
import org.gongxuanzhang.mysql.service.executor.Executor
import org.gongxuanzhang.mysql.service.token.SqlTokenizer
import org.gongxuanzhang.mysql.tool.ApplicationUtils
import org.gongxuanzhang.mysql.tool.SqlUtils


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/

fun String.doSql(): Result {
//    val tokenAnalysis = SubSqlAnalysis()
//    tokenAnalysis.init()
//    val tokenizer = SqlTokenizer(this)
//    val process = tokenizer.process()
//    val executor: Executor = tokenAnalysis.analysis(process)
//    return executor.doExecute()
    val applicationContext = ApplicationUtils.applicationContext
    val sqlAnalysis = applicationContext.getBean(SqlAnalysis::class.java)
    return sqlAnalysis.analysis(this).doExecute()
}


