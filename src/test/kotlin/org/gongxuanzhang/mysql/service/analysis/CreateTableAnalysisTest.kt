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

package org.gongxuanzhang.mysql.service.analysis

import org.gongxuanzhang.mysql.doSql
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
@SpringBootTest
class CreateTableAnalysisTest {


    @Test
    fun createTableTest(){
        """
            CREATE TABLE aa.example_table (
              id INT UNSIGNED primary key NOT NULL AUTO_INCREMENT COMMENT '主键ID',
              name VARCHAR(50) NOT NULL DEFAULT '' COMMENT '姓名',
              age INT NOT NULL DEFAULT 0 COMMENT '年龄',
              email VARCHAR(100) NOT NULL UNIQUE COMMENT '电子邮箱',
              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
              updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
              PRIMARY KEY (id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='示例表';
        """.trimIndent().doSql()
    }
}
