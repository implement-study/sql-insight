/*
 * Copyright 2023 java-mysql  and the original author or authors <gongxuanzhangmelt@gmail.com>.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/implement-study/java-mysql/blob/main/LICENSE
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


    @Test
    fun simpleCreateDatabase() {
        val testGod = TestGod()
        testGod.prepareGodTable()
        """
            insert into ${testGod.fullName} (id,name,gender,age,id_card) 
            values(1,'zhangsan','man',12,'abcd')
        """.doSql()
    }


}
