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

import com.alibaba.druid.sql.SQLUtils
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement
import org.gongxuanzhang.sql.insight.core.analysis.ddl.CreateTableAdaptor
import org.junit.jupiter.api.Test


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class CreateTableTest {

    private val pipeline = InsightFactory.createSqlPipeline()

    private val sql = """
        CREATE TABLE db.custom_constraints (
            id INT PRIMARY KEY AUTO_INCREMENT,
            name VARCHAR(50) AUTO_INCREMENT,
            age INT,
            email VARCHAR(100),
            CHECK (age >= 18 AND age <= 100), -- 使用CHECK约束限制age的范围
            CONSTRAINT check_email_format CHECK (email LIKE '%@'),
            UNIQUE (name),
            CONSTRAINT custom_unique_age_email UNIQUE (age, email),
            FOREIGN KEY (id) REFERENCES another_table(id),
            CONSTRAINT custom_fk_name FOREIGN KEY (name) REFERENCES another_table(name) ON DELETE CASCADE
        );

    """.trimIndent()

    @Test
    fun testAnalysis() {
        val statement = SQLUtils.parseSingleMysqlStatement(sql)
        val createTable = CreateTableAdaptor().adaptor(sql, statement as MySqlCreateTableStatement)
        val table = createTable.table
        assert(table.name == "db")
    }

    @Test
    fun testCreateTable() {
        pipeline.doSql(sql)
    }


}

