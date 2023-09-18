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

package org.gongxuanzhang.sql.insight.core.analysis

import org.gongxuanzhang.sql.insight.core.analysis.druid.DruidAnalyzer
import org.gongxuanzhang.sql.insight.core.command.ddl.CreateDatabase
import org.junit.jupiter.api.Test


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class AnalysisTest {

    private var analyzer: Analyzer = DruidAnalyzer()

    private var createDbSql: String = "create database testDb"

    @Test
    fun createDb() {
        val analysisSql = analyzer.analysisSql(createDbSql)
        assertType(analysisSql, CreateDatabase::class.java)
    }


    private fun assertType(obj: Any, type: Class<*>) {
        assert(type.isAssignableFrom(obj::class.java)) {
            """
                ${obj.javaClass} is not ${type.name} 
            """.trimIndent()
        }
    }
}
