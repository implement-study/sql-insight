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
import org.gongxuanzhang.mysql.exception.ExecuteException
import org.gongxuanzhang.mysql.tool.Context
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import java.io.File


/**
 * @author gongxuanzhang
 */
@SpringBootTest
class DeleteTest {

    var database: String = "testDatabase"

    @Test
    @DisplayName("删除普通数据库")
    fun simpleCreateDatabase() {
        "delete from a.a where a = 1".doSql()
    }




}
