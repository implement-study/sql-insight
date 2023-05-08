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
import org.gongxuanzhang.mysql.service.executor.ddl.table.CreateTableTest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest


/**
 * @author gongxuanzhang
 */
@SpringBootTest
class InsertTest {

    var database: String = "testDatabase"

    @Test
    fun simpleCreateDatabase() {
        CreateTableTest().createTableTest()
        """
            insert into create_database.create_test_table_user (id,age,name,gender,id_card) 
            values(1,18,'zhangsan','男','abcd'),
               (2,24,'lisi','女','abcd'),
               (3,25,'wangwu',NULL,'afff')
            
        """.doSql()
    }




}
