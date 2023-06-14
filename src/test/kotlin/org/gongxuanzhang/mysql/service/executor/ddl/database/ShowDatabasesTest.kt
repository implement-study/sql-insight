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

package org.gongxuanzhang.mysql.service.executor.ddl.database

import org.gongxuanzhang.mysql.core.result.SingleRowResult
import org.gongxuanzhang.mysql.destructuringEquals
import org.gongxuanzhang.mysql.doSql
import org.gongxuanzhang.mysql.tool.Context
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.io.FileFilter


/**
 * @author gongxuanzhang
 */
@SpringBootTest
class ShowDatabasesTest {


    @Test
    fun showDatabases() {
        val testDatabases = arrayOf("test1", "test2", "test3")
        testDatabases.forEach {
            "create database $it".doSql()
        }
        val databases = Context.getHome().listFiles(FileFilter { it.isDirectory })
        val doSql = "show databases".doSql()
        assert((doSql as SingleRowResult).destructuringEquals(databases?.map { it.name }))
        testDatabases.forEach {
            "drop database $it".doSql()
        }
    }


}
