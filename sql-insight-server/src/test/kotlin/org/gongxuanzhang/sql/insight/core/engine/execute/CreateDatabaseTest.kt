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

import org.gongxuanzhang.sql.insight.assertFalse
import org.gongxuanzhang.sql.insight.core.engine.json.InsightFactory
import org.gongxuanzhang.sql.insight.core.exception.DatabaseExistsException
import org.gongxuanzhang.sql.insight.databaseFile
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class CreateDatabaseTest {

    @Test
    fun testCreateDatabase() {
        val dbFolder = databaseFile("sql_insight_test_db")
        if (dbFolder.exists()) {
            dbFolder.deleteRecursively()
        }
        assertFalse(dbFolder.exists(), "${dbFolder.absolutePath} exists and that can't deleted")
        val sql = "create database sql_insight_test_db"
        val createSqlPipeline = InsightFactory.createSqlPipeline()
        createSqlPipeline.doSql(sql)
        assert(dbFolder.exists())
        dbFolder.deleteRecursively()
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

