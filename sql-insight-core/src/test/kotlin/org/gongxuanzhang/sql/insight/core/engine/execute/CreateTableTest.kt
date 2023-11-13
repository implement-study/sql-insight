/*
 * Copyright 2023 java-mysql  and the original author or authors <gongxuanzhangmelt@gmail.com>.
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

import org.gongxuanzhang.sql.insight.*
import org.gongxuanzhang.sql.insight.core.command.ddl.CreateTable
import org.gongxuanzhang.sql.insight.core.exception.DatabaseNotExistsException
import org.gongxuanzhang.sql.insight.core.`object`.Column
import org.gongxuanzhang.sql.insight.core.`object`.DataType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class CreateTableTest {


    private fun createTableSql(tableName: String, databaseName: String = "", ifNotExists: Boolean = true): String {
        return """
                create table ${if (ifNotExists) "IF NOT EXISTS" else ""}
                 ${if (databaseName.isEmpty()) databaseName else "$databaseName."}$tableName
                (
                id int primary key auto_increment,
                name varchar not null,
                gender varchar(20) default '男' not null comment '性别',
                id_card char UNIQUE
                ) comment = '用户表', engine = 'innodb'
            """.trimIndent()
    }


    @Test
    fun testCommand() {
        val tableName = "test_tableName"
        val databaseName = "test_database_name"
        val createTable = createTableSql(tableName, databaseName).toCommand()
        val table = (createTable as CreateTable).table
        assertEquals(table.name, tableName)
        assertEquals(table.database.name, databaseName)
        assertEquals(table.comment, "用户表")
        assertEquals(table.columnList[0], run {
            val column = Column()
            column.isAutoIncrement = true
            column.isPrimaryKey = true
            column.name = "id"
            val dataType = DataType()
            dataType.type = DataType.Type.INT
            dataType.length = 8
            column.dataType = dataType
            column
        })
        assertEquals(table.columnList[1], run {
            val column = Column()
            column.isNotNull = true
            column.name = "name"
            val dataType = DataType()
            dataType.type = DataType.Type.VARCHAR
            dataType.length = 255
            column.dataType = dataType
            column
        })
        assertEquals(table.columnList[2], run {
            val column = Column()
            column.isNotNull = true
            column.name = "gender"
            val dataType = DataType()
            dataType.type = DataType.Type.VARCHAR
            dataType.length = 20
            column.defaultValue = "男"
            column.dataType = dataType
            column.comment = "性别"
            column
        })
        assertEquals(table.columnList[3], run {
            val column = Column()
            column.name = "id_card"
            val dataType = DataType()
            dataType.type = DataType.Type.CHAR
            dataType.length = 255
            column.dataType = dataType
            column.isUnique = true
            column
        })
    }


    @Test
    fun testCreateTableDbNotExists() {
        val tableName = "test_tableName"
        val databaseName = "test_database_name"
        clearDatabase(databaseName)
        assertThrows<DatabaseNotExistsException> { createTableSql(tableName, databaseName, true).doSql() }
    }


    @Test
    fun testCreateTable() {
        val tableName = "test_tableName"
        val databaseName = "test_database_name"
        createDatabase(databaseName)
        createTableSql(tableName, databaseName, true).doSql()
        assert(File(databaseFile(databaseName), "$tableName.frm").exists())
        clearDatabase(databaseName)

    }


}

