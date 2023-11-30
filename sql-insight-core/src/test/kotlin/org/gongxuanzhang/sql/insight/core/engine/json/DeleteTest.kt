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

import org.gongxuanzhang.sql.insight.core.command.dml.Delete
import org.gongxuanzhang.sql.insight.core.environment.SqlInsightContext
import org.gongxuanzhang.sql.insight.doSql
import org.gongxuanzhang.sql.insight.forEachLineIndex
import org.gongxuanzhang.sql.insight.insert
import org.gongxuanzhang.sql.insight.toCommand
import org.junit.jupiter.api.Test


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class DeleteTest {


    @Test
    fun testCommand() {
        val toCommand = "delete from aa.user  where 'id' >1 and id <1 and id = 1".toCommand()
        assert(toCommand is Delete)
    }

    @Test
    fun testDelete() {
        insert("aa", "user")
        "delete from aa.user  where id>2 ".doSql()
        val table = SqlInsightContext.getInstance().tableDefinitionManager.select("aa", "user")
        val tableJson = JsonEngineSupport.getJsonFile(table)
        tableJson.forEachLineIndex { index, line ->
            if (index > 3) {
                assert(line.isEmpty())
            }
        }
    }


}

