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

package org.gongxuanzhang.sql.insight.core.engine.execute

import org.gongxuanzhang.sql.insight.clearDatabase
import org.gongxuanzhang.sql.insight.core.environment.SqlInsightContext
import org.gongxuanzhang.sql.insight.core.exception.TableNotExistsException
import org.gongxuanzhang.sql.insight.createTable
import org.gongxuanzhang.sql.insight.doSql
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class DropTableTest {


    private fun dropTableSql(tableName: String, databaseName: String = "", ifNotExists: Boolean = true): String {
        return """
                drop table ${if (ifNotExists) "IF EXISTS" else ""}
                 ${if (databaseName.isEmpty()) databaseName else "$databaseName."}$tableName
            """.trimIndent()
    }


    @Test
    fun testDropTable() {
        val databaseName = "test_database"
        val tableName = "test_table"
        createTable(databaseName, tableName)
        dropTableSql(tableName, databaseName, true).doSql()
        assert(SqlInsightContext.getInstance().tableDefinitionManager.select(databaseName, tableName) == null)
        clearDatabase(databaseName)
    }


    @Test
    fun testDropNotExists() {
        val databaseName = "test_database_not_exists"
        val tableName = "test_table_not_exists"
        clearDatabase(databaseName)
        assertThrows<TableNotExistsException> { dropTableSql(tableName, databaseName, false).doSql() }
        dropTableSql(tableName, databaseName, true).doSql()
    }


}

