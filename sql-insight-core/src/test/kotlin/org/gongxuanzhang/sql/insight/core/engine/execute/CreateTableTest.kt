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

import org.gongxuanzhang.sql.insight.core.command.ddl.CreateTable
import org.gongxuanzhang.sql.insight.core.`object`.Column
import org.gongxuanzhang.sql.insight.core.`object`.DataType
import org.gongxuanzhang.sql.insight.doSql
import org.gongxuanzhang.sql.insight.toCommand
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class CreateTableTest {


    private val sql = """
        CREATE TABLE custom_constraints (
            id int PRIMARY KEY AUTO_INCREMENT,
            name VARCHAR(50) AUTO_INCREMENT default 'sdf',
            age INT default 1 unique comment '年龄',
            email VARCHAR(100) not null,
            alias char(10),
            CHECK (age >= 18 AND age <= 100), -- 使用CHECK约束限制age的范围
            CONSTRAINT check_email_format CHECK (email LIKE '%@'),
            UNIQUE (name),
            CONSTRAINT custom_unique_age_email UNIQUE (age, email),
            FOREIGN KEY (id) REFERENCES another_table(id),
            CONSTRAINT custom_fk_name FOREIGN KEY (name) REFERENCES another_table(name) ON DELETE CASCADE
        ) comment = 'asdf'

    """.trimIndent()

    @Test
    fun testAnalysis() {
    }

    @Test
    fun testCreateTable() {
        sql.doSql()
    }


//    @Test
//    fun createSessionTable() {
//        database = "create_database"
//        CreateDatabaseTest().doCreateDatabase(database)
//        val tableName = "aaa"
//        UseDatabaseTest().doUseDatabase(database)
//        doCreateSessionTable(tableName)
//        val select = Context.getTableManager().select("$database.$tableName")
//        checkTableInfo(select, database, tableName)
//    }
//
//    @Test
//    fun createExistTable() {
//        database = "create_database"
//        CreateDatabaseTest().doCreateDatabase(database)
//        doCreateTable(database, "aaa")
//        assertThrows<ExecuteException> {
//            doCreateTable(database, "aaa")
//        }
//    }
//
//    @Test
//    fun createNoExistDatabase() {
//        assertThrows<ExecuteException> {
//            doCreateTable("aaa", "aaaaa")
//        }
//    }


    fun createTableSql(tableName: String, databaseName: String = "", ifNotExists: Boolean = true): String {
        return """
                                    create table ${if (ifNotExists) "IF NOT EXISTS" else ""}
                                     ${if (databaseName.isEmpty()) databaseName else "$databaseName."}$tableName
                                    (
                                    id int primary key auto_increment,
                                    name varchar not null,
                                    gender varchar default '张三' not null,
                                    age int comment '年龄',
                                    id_card varchar UNIQUE
                                    ) comment = 用户表
                                """.trimIndent()
    }


    @Test
    fun doCreateTable() {
        val tableName = "test_tableName"
        val databaseName = "test_database_name"
        val createTable = createTableSql(tableName, databaseName).toCommand()
        val table = (createTable as CreateTable).table
        assertEquals(table.name,tableName)
        assertEquals(table.database.name,databaseName)
        assertEquals(table.comment,"用户表")
        assertEquals(table.columnList[0],run {
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
    }


//    private fun checkTableInfo(select: TableInfo, database: String, tableName: String) {
//        Assertions.assertEquals(select.database.databaseName, database)
//        Assertions.assertEquals(select.tableName, tableName)
//        Assertions.assertEquals(select.comment, "用户表")
//        assertEquals(select.columns[0], run {
//            val column = Column()
//            column.name = "id"
//            column.type = ColumnType.INT
//            column.isAutoIncrement = true
//            column
//        })
//        assertEquals(select.columns[1], run {
//            val column = Column()
//            column.name = "name"
//            column.type = ColumnType.VARCHAR
//            column.isNotNull = true
//            column.length = 200
//            column
//        })
//        assertEquals(select.columns[2], run {
//            val column = Column()
//            column.name = "gender"
//            column.type = ColumnType.VARCHAR
//            column.isNotNull = true
//            column.defaultValue = StringDefaultValue("张三")
//            column.length = 200
//            column
//        })
//        assertEquals(select.columns[3], run {
//            val column = Column()
//            column.name = "age"
//            column.type = ColumnType.INT
//            column.comment = "年龄"
//            column
//        })
//        assertEquals(select.columns[4], run {
//            val column = Column()
//            column.name = "id_card"
//            column.type = ColumnType.VARCHAR
//            column.isUnique = true
//            column
//        })
//        Assertions.assertEquals(select.comment, "用户表")
//        Assertions.assertEquals(select.primaryKey, arrayListOf("id"))
//    }


}

