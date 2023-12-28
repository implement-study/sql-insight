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
import org.gongxuanzhang.sql.insight.core.command.dml.Select
import org.gongxuanzhang.sql.insight.core.result.SelectResult
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class SelectTest {

    @AfterEach
    fun clearData() {
        clearDatabase("aa")
    }

    @Test
    fun selectCommand() {
        insert("aa", "user")
        val toCommand = "select * from aa.user where id>1 order by id limit 1,2 ".toCommand()
        assert(toCommand is Select)
    }

    @Test
    fun selectTest() {
        insert("aa", "user")
        "select * from aa.user where id>1".doSql()
    }

    @Test
    fun selectOrderBy() {
        insertLarge("aa", "user", 50)
        val doSql = "select * from aa.user where id > 10 order by name asc".doSql()
        assert(doSql is SelectResult)
        val result = (doSql as SelectResult).result
        result.forEach { println(it) }
    }

    @Test
    fun selectLimit() {
        insertLarge("aa", "user", 50)
        val doSql = "select * from aa.user where id > 10 limit 4,10 ".doSql()
        assert(doSql is SelectResult)
        val result = (doSql as SelectResult).result
        assert(result.size == 10)
        assert(result[0].rowId..result[result.size - 1].rowId == 15L..24)
    }


}

