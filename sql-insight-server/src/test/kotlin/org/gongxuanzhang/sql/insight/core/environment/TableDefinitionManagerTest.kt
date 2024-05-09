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

package org.gongxuanzhang.sql.insight.core.environment

import org.gongxuanzhang.sql.insight.core.`object`.Database
import org.gongxuanzhang.sql.insight.core.`object`.Table
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class TableDefinitionManagerTest {


    @Test
    fun testRegister() {
        val tableName = "test_table"
        val databaseName = "test_db"
        val tableDefinitionManager = TableDefinitionManager()
        val table = Table()
        table.name = tableName
        table.database = Database(databaseName)
        assertNull(tableDefinitionManager.select(databaseName, tableName))
        tableDefinitionManager.load(table)
        assertNotNull(tableDefinitionManager.select(databaseName, tableName))
        assertEquals(1, tableDefinitionManager.select(databaseName).size)
        tableDefinitionManager.unload(table)
        assertNull(tableDefinitionManager.select(databaseName, tableName))
        assertEquals(0, tableDefinitionManager.select(databaseName).size)
        val database = Database(databaseName)
        for (i in 1..10) {
            val itemTable = Table()
            itemTable.name = "$tableName$i"
            itemTable.database = database
            tableDefinitionManager.load(itemTable)
        }
        assertEquals(10, tableDefinitionManager.select(databaseName).size)
        tableDefinitionManager.unload(database)
        assertEquals(0, tableDefinitionManager.select(databaseName).size)

    }
}
