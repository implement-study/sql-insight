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

package org.gongxuanzhang.sql.insight.core.engine.storage.execute

import org.gongxuanzhang.sql.insight.*
import org.gongxuanzhang.sql.insight.core.command.dml.Insert
import org.gongxuanzhang.sql.insight.core.engine.json.InsightFactory
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.index.InnodbIndex
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.utils.PageSupport
import org.gongxuanzhang.sql.insight.core.environment.SqlInsightContext
import org.gongxuanzhang.sql.insight.core.exception.DatabaseExistsException
import org.gongxuanzhang.sql.insight.core.exception.TableNotExistsException
import org.gongxuanzhang.sql.insight.core.`object`.value.ValueInt
import org.gongxuanzhang.sql.insight.core.`object`.value.ValueVarchar
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class InsertTest {

    @AfterEach
    fun clear() {
        clearDatabase("aa")
    }

    @Test
    fun testInsert() {
        insert("aa", "user")
        val table = context().tableDefinitionManager.select("aa", "user")
        val context = SqlInsightContext.getInstance()
        val engine = context.selectEngine("innodb")
        engine.openTable(table)
        assert(table.indexList.size == 1)
        val clusterIndex = table.indexList[0]
        val rootPage = PageSupport.getRoot(clusterIndex as InnodbIndex)
        rootPage.forEach {
            println(it)
        }

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

