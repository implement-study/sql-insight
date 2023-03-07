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
import org.gongxuanzhang.mysql.entity.ColumnInfo
import org.gongxuanzhang.mysql.entity.ColumnType
import org.gongxuanzhang.mysql.entity.TableInfo
import org.gongxuanzhang.mysql.exception.ExecuteException
import org.gongxuanzhang.mysql.service.executor.ddl.database.CreateDatabaseTest
import org.gongxuanzhang.mysql.service.executor.ddl.database.DropDatabaseTest
import org.gongxuanzhang.mysql.service.executor.ddl.database.UseDatabaseTest
import org.gongxuanzhang.mysql.tool.Context
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
@SpringBootTest
class CreateTableTest {


    @Test
    fun createTableTest() {
        val database = "create_database"
        CreateDatabaseTest().doCreateDatabase(database)
        val tableName = "create_test_table_user"
        doCreateTable(database, tableName)
        val select = Context.getTableManager().select("$database.$tableName")
        checkTableInfo(select, database, tableName)
        DropDatabaseTest().doDropDatabase(database)
    }


    @Test
    fun createSessionTable() {
        val database = "create_database"
        CreateDatabaseTest().doCreateDatabase(database)
        val tableName = "aaa"
        UseDatabaseTest().doUseDatabase(database)
        doCreateSessionTable(tableName)
        val select = Context.getTableManager().select("$database.$tableName")
        checkTableInfo(select, database, tableName)
        DropDatabaseTest().doDropDatabase(database)
    }

    @Test
    fun createExistTable() {
        val database = "create_database"
        CreateDatabaseTest().doCreateDatabase(database)
        doCreateTable(database, "aaa")
        assertThrows<ExecuteException> {
            doCreateTable(database, "aaa")
        }
        DropDatabaseTest().doDropDatabase(database)
    }

    @Test
    fun createNoExistDatabase() {
        assertThrows<ExecuteException> {
            doCreateTable("aaa", "aaaaa")
        }
    }


    fun doCreateSessionTable(tableName: String): Result {
        return """
                    create table $tableName(
                    id int primary key auto_increment,
                    name varchar not null,
                    gender varchar default '张三' not null,
                    age int comment '年龄',
                    id_card varchar UNIQUE,
                    ) comment ='用户表'
                """.doSql()
    }

    fun doCreateTable(database: String, tableName: String): Result {
        return """
                    create table $database.$tableName(
                    id int primary key auto_increment,
                    name varchar(200) not null,
                    gender varchar(200) default '张三' not null,
                    age int comment '年龄',
                    id_card varchar UNIQUE
                    ) comment ='用户表'
                """.doSql()
    }


    private fun checkTableInfo(select: TableInfo, database: String, tableName: String) {
        assert(select.database.databaseName == database)
        assert(select.tableName == tableName)
        assert(select.comment == "用户表")
        assert(select.columnInfos[0] == run {
            val columnInfo = ColumnInfo()
            columnInfo.name = "id"
            columnInfo.type = ColumnType.INT
            columnInfo.isAutoIncrement = true
            columnInfo
        })
        assert(select.columnInfos[1] == run {
            val columnInfo = ColumnInfo()
            columnInfo.name = "name"
            columnInfo.type = ColumnType.VARCHAR
            columnInfo.isNotNull = true
            columnInfo
        })
        assert(select.columnInfos[2] == run {
            val columnInfo = ColumnInfo()
            columnInfo.name = "gender"
            columnInfo.type = ColumnType.VARCHAR
            columnInfo.isNotNull = true
            columnInfo.defaultValue = "张三"
            columnInfo
        })
        assert(select.columnInfos[3] == run {
            val columnInfo = ColumnInfo()
            columnInfo.name = "age"
            columnInfo.type = ColumnType.INT
            columnInfo.comment = "年龄"
            columnInfo
        })
        assert(select.columnInfos[4] == run {
            val columnInfo = ColumnInfo()
            columnInfo.name = "id_card"
            columnInfo.type = ColumnType.VARCHAR
            columnInfo.isUnique = true
            columnInfo
        })
        assert(select.comment == "用户表")
        assert(select.primaryKey == arrayListOf("id"))
    }

}
