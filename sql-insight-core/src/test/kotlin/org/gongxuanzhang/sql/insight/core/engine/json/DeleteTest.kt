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

package org.gongxuanzhang.sql.insight.core.engine.json

import com.alibaba.fastjson2.JSONObject
import org.gongxuanzhang.sql.insight.clearDatabase
import org.gongxuanzhang.sql.insight.core.command.dml.Delete
import org.gongxuanzhang.sql.insight.core.environment.SqlInsightContext
import org.gongxuanzhang.sql.insight.createTable
import org.gongxuanzhang.sql.insight.doSql
import org.gongxuanzhang.sql.insight.toCommand
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class DeleteTest {

    @Test
    fun testInsert() {
        createTable("aa", "user")
        """insert into aa.user (id,name) values
            (1,'a') ,(2,'b') ,(null,'c')
            ,(null,'b') ,(null,'c')
        """.trimMargin().doSql()
        val context = SqlInsightContext.getInstance()
        val table = context.tableDefinitionManager.select("aa", "user")
        val tableJson = JsonEngineSupport.getJsonFile(table)
        val jsonList = mutableListOf<JSONObject>()
        tableJson.forEachLine {
            if (it.isNotEmpty()) {
                val jsonObject = JSONObject.parse(it)
                jsonList.add(jsonObject)
            }
        }
        assertEquals(
            jsonList, listOf(
                JSONObject.of("id", 1, "name", "a"),
                JSONObject.of("id", 2, "name", "b"),
                JSONObject.of("id", 3, "name", "c"),
                JSONObject.of("id", 4, "name", "b"),
                JSONObject.of("id", 5, "name", "c"),
            )
        )
        clearDatabase("aa")
    }


    @Test
    fun testCommand() {
        createTable("aa", "user")
        val toCommand = "delete from aa.user  where 'id' >1 and id <1 and id = 1".toCommand()
        val delete = toCommand as Delete
    }



}

