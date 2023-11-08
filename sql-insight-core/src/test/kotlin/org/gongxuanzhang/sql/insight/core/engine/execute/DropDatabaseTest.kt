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
import org.gongxuanzhang.sql.insight.core.exception.DatabaseNotExistsException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class DropDatabaseTest {
    private val dbName = "sql_insight_test_db"
    private val pipeline = InsightFactory.createSqlPipeline()
    private val dbFolder = File(dbName)

    @Test
    fun testDropDatabase() {

        if (!dbFolder.exists()) {
            dbFolder.mkdirs()
        }
        assertFalse(!dbFolder.exists(), "${dbFolder.absolutePath} can't created")
        val sql = "drop database $dbName"
        pipeline.doSql(sql)
        assertFalse(dbFolder.exists())
        assertThrows<DatabaseNotExistsException> { pipeline.doSql(sql) }
    }

    @Test
    fun testDropDatabaseNotExists() {
        testDropDatabase()
        val sql = "drop database if exists $dbName"
        pipeline.doSql(sql)
        assertFalse(dbFolder.exists())

    }


}

