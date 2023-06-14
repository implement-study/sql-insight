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
import org.gongxuanzhang.mysql.tool.TestGod
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest


/**
 * @author gongxuanzhangmelt@gmail.com
 */
@SpringBootTest
class InsertTest {

    var database: String = "testDatabase"

    @Test
    fun simpleInsert() {
        val testGod = TestGod()
        testGod.prepareGodTable()
        val insertResult = """
            insert into ${testGod.fullName} (id,age,name,gender,id_card) 
            values
               (800,24,'lisi','女','abcd'),
               (801,25,'wangwu','女','afff')
        """.doSql()
    }

    @Test
    fun batchInsert() {
        val testGod = TestGod()
        testGod.prepareGodTable()
        for (i in 0..600 step 2) {
            """
            insert into ${testGod.fullName} (id,age,name,gender,id_card) 
            values
               (${i},24,'lisi','女','abcd'),
               (${i+1},25,'wangwu','女','afff')
        """.doSql()
        }

    }


}
