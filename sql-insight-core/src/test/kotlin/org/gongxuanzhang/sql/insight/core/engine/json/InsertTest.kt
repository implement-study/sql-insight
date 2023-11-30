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
import org.gongxuanzhang.sql.insight.*
import org.gongxuanzhang.sql.insight.core.command.dml.Insert
import org.gongxuanzhang.sql.insight.core.environment.SqlInsightContext
import org.gongxuanzhang.sql.insight.core.exception.DatabaseExistsException
import org.gongxuanzhang.sql.insight.core.exception.TableNotExistsException
import org.gongxuanzhang.sql.insight.core.`object`.value.ValueInt
import org.gongxuanzhang.sql.insight.core.`object`.value.ValueVarchar
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class InsertTest {

    @Test
    fun testInsert() {
        insert("aa", "user")
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
        val toCommand = "insert into aa.user (id,name) values(1,'a') ,(2,'b')".toCommand()
        val insert = toCommand as Insert
        assertEquals(insert.insertRows[0].values[0], ValueInt(1))
        assertEquals(insert.insertRows[0].values[1], ValueVarchar("a"))
        assertEquals(insert.insertRows[1].values[0], ValueInt(2))
        assertEquals(insert.insertRows[1].values[1], ValueVarchar("b"))
        assertEquals(insert.insertColumns.map { it.name }, listOf("id", "name"))
        clearDatabase("aa")
    }

    @Test
    fun testNotFound() {
        assertThrows<TableNotExistsException> { "insert into aa.user (id,name) values(1,'a') ,(2,'b')".toCommand() }
    }

    @Test
    fun testCreateDatabaseExists() {
        val dbFolder = databaseFile("sql_insight_test_db")
        if (!dbFolder.exists()) {
            dbFolder.mkdirs()
        }
        val sql = "create database sql_insight_test_db"
        val createSqlPipeline = InsightFactory.createSqlPipeline()
        assertThrows<DatabaseExistsException> { createSqlPipeline.doSql(sql) }
        dbFolder.deleteRecursively()
    }

    @Test
    fun testCreateDatabaseExistsIfNotExists() {
        val dbFolder = databaseFile("sql_insight_test_db")
        if (!dbFolder.exists()) {
            dbFolder.mkdirs()
        }
        val sql = "create database IF NOT EXISTS  sql_insight_test_db"
        val createSqlPipeline = InsightFactory.createSqlPipeline()
        createSqlPipeline.doSql(sql)
        dbFolder.deleteRecursively()
    }


}

