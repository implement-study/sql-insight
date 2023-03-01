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

package org.gongxuanzhang.mysql.service.executor.ddl.database

import org.gongxuanzhang.mysql.doSql
import org.gongxuanzhang.mysql.exception.ExecuteException
import org.gongxuanzhang.mysql.tool.Context
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import java.io.File


/**
 * @author gongxuanzhang
 */
@SpringBootTest
class CreateDatabaseTest {

    var database: String = "testDatabase"

    @Test
    @DisplayName("普通创建数据库")
    fun simpleCreateDatabase() {
        doCreateDatabase(database)
        val home = Context.getHome()
        val file = File(home, database)
        assert(file.exists())
        file.deleteRecursively()
    }

    fun doCreateDatabase(database: String) {
        "create database $database".doSql()
    }


    @Test
    @DisplayName("创建已经存在的数据库")
    fun createExistDatabase() {
        val file = File(Context.getHome(), this.database)
        file.mkdirs()
        assertThrows<ExecuteException> {
            "create database $database".doSql()
        }
        file.deleteRecursively()


    }

}
