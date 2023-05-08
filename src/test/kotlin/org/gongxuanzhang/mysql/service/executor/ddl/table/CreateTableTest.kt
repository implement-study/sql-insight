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

package org.gongxuanzhang.mysql.service.executor.ddl.table

import org.gongxuanzhang.mysql.core.result.Result
import org.gongxuanzhang.mysql.doSql
import org.gongxuanzhang.mysql.entity.Column
import org.gongxuanzhang.mysql.entity.ColumnType
import org.gongxuanzhang.mysql.entity.StringDefaultValue
import org.gongxuanzhang.mysql.entity.TableInfo
import org.gongxuanzhang.mysql.exception.ExecuteException
import org.gongxuanzhang.mysql.service.executor.ddl.database.CreateDatabaseTest
import org.gongxuanzhang.mysql.service.executor.ddl.database.DropDatabaseTest
import org.gongxuanzhang.mysql.service.executor.ddl.database.UseDatabaseTest
import org.gongxuanzhang.mysql.tool.Context
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
@SpringBootTest
class CreateTableTest {


    companion object {

        var database: String = ""

        @AfterAll
        @JvmStatic
        fun dropDatabase() {
            print(database)
            DropDatabaseTest().doDropDatabaseIsNotExists(database)
        }
    }


    @Test
    fun createTableTest() {
        database = "create_database"
        CreateDatabaseTest().doCreateDatabase(database)
        val tableName = "create_test_table_user"
        doCreateTable(database, tableName)
        val select = Context.getTableManager().select("$database.$tableName")
        checkTableInfo(select, database, tableName)
        // checkInnodbPage()
    }

    private fun checkInnodbPage() {
        TODO("Not yet implemented")
    }


    @Test
    fun createSessionTable() {
        database = "create_database"
        CreateDatabaseTest().doCreateDatabase(database)
        val tableName = "aaa"
        UseDatabaseTest().doUseDatabase(database)
        doCreateSessionTable(tableName)
        val select = Context.getTableManager().select("$database.$tableName")
        checkTableInfo(select, database, tableName)
    }

    @Test
    fun createExistTable() {
        database = "create_database"
        CreateDatabaseTest().doCreateDatabase(database)
        doCreateTable(database, "aaa")
        assertThrows<ExecuteException> {
            doCreateTable(database, "aaa")
        }
    }

    @Test
    fun createNoExistDatabase() {
        assertThrows<ExecuteException> {
            doCreateTable("aaa", "aaaaa")
        }
    }


    fun doCreateSessionTable(tableName: String): Result {
        return """
                    create table IF NOT EXISTS $tableName(
                    id int primary key auto_increment,
                    name varchar not null,
                    gender varchar default '张三' not null,
                    age int comment '年龄',
                    id_card varchar UNIQUE
                    ) comment ='用户表'
                """.doSql()
    }

    fun doCreateTable(database: String, tableName: String): Result {
        return """
                    create table `$database.$tableName`(
                    `id` int primary key auto_increment,
                    name varchar(200) not null,
                    gender varchar(200) default '张三' not null,
                    age int comment '年龄',
                    id_card varchar UNIQUE
                    ) comment =`用户表`
                """.doSql()
    }


    private fun checkTableInfo(select: TableInfo, database: String, tableName: String) {
        assertEquals(select.database.databaseName, database)
        assertEquals(select.tableName, tableName)
        assertEquals(select.comment, "用户表")
        assertEquals(select.columnInfos[0], run {
            val column = Column()
            column.name = "id"
            column.type = ColumnType.INT
            column.isAutoIncrement = true
            column
        })
        assertEquals(select.columnInfos[1], run {
            val column = Column()
            column.name = "name"
            column.type = ColumnType.VARCHAR
            column.isNotNull = true
            column.length = 200
            column
        })
        assertEquals(select.columnInfos[2], run {
            val column = Column()
            column.name = "gender"
            column.type = ColumnType.VARCHAR
            column.isNotNull = true
            column.defaultValue = StringDefaultValue("张三")
            column.length = 200
            column
        })
        assertEquals(select.columnInfos[3], run {
            val column = Column()
            column.name = "age"
            column.type = ColumnType.INT
            column.comment = "年龄"
            column
        })
        assertEquals(select.columnInfos[4], run {
            val column = Column()
            column.name = "id_card"
            column.type = ColumnType.VARCHAR
            column.isUnique = true
            column
        })
        assertEquals(select.comment, "用户表")
        assertEquals(select.primaryKey, arrayListOf("id"))
    }

}
