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

package org.gongxuanzhang.mysql.service.executor.dml

import org.gongxuanzhang.mysql.doSql
import org.gongxuanzhang.mysql.tool.Console
import org.gongxuanzhang.mysql.tool.TestGod
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest


/**
 * @author gongxuanzhang
 */
@SpringBootTest
class SelectTest {

    var database: String = "testDatabase"

    @Test
    fun selectAll() {
        val testGod = TestGod()
        testGod.prepareGodTable()
        val doSql = "select * from ${testGod.fullName}".doSql()
        Console.infoResult(doSql)
    }




}
