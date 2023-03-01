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

import org.gongxuanzhang.mysql.doSql
import org.gongxuanzhang.mysql.exception.MySQLException
import org.gongxuanzhang.mysql.tool.Context
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import java.io.File
import java.io.FileFilter


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
@SpringBootTest
class DropTableTest {


    @Test
    fun dropTable() {
        val database = "create_database"

        "create database $database".doSql()
        val tableName = "table_test"

        """
            create table $database.$tableName(
            id int primary key auto_increment,
            name varchar not null,
            gender varchar default '张三' not null,
            age int comment '年龄',
            id_card varchar UNIQUE,
            ) comment ='用户表'
        """.doSql()
        Context.getTableManager().select("$database.$tableName")
        "drop table $database.$tableName".doSql()
        //  内存中删除
        assertThrows<MySQLException> {
            Context.getTableManager().select("$database.$tableName")
        }
        //  硬盘上删除
        val dbDir = File(Context.getHome(), database)
        val tableFiles = dbDir.listFiles(FileFilter { it.name.contains(tableName) })
        assert(tableFiles?.isEmpty() == true)
        "drop database $database".doSql()
    }

    @Test
    fun dropNoExistTable() {
        val database = "create_database"

        "create database $database".doSql()

        assertThrows<MySQLException> {
            "drop table $database.aaaaaaaaa".doSql()
        }

        "drop database $database".doSql()

    }

}
