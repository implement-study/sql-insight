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

package org.gongxuanzhang.sql.insight.core.engine.execute

import org.gongxuanzhang.sql.insight.*
import org.gongxuanzhang.sql.insight.core.command.dml.Delete
import org.gongxuanzhang.sql.insight.core.environment.SqlInsightContext
import org.gongxuanzhang.sql.insight.core.exception.TableNotExistsException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class DeleteTest {

    @AfterEach
    fun clearData() {
        clearDatabase("aa")
    }

    @Test
    fun testCommand() {
        clearDatabase("aa")
        val sql = "delete from aa.user  where 'id' >1 and id <1 and id = 1"
        assertThrows<TableNotExistsException> { sql.toCommand() }
        createTable("aa", "user")
        val toCommand = sql.toCommand()
        assert(toCommand is Delete)
    }

    @Test
    fun testDelete() {
        insert("aa", "user")
        "delete from aa.user  where id>2 ".doSql()
        val table = SqlInsightContext.getInstance().tableDefinitionManager.select("aa", "user")
    }


}

