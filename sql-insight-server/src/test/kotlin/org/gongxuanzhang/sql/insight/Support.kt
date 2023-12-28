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

package org.gongxuanzhang.sql.insight

import org.gongxuanzhang.sql.insight.core.environment.DefaultProperty
import org.gongxuanzhang.sql.insight.core.environment.GlobalContext
import org.gongxuanzhang.sql.insight.core.environment.SqlInsightContext
import java.io.File
import kotlin.random.Random


fun databaseFile(database: String): File {
    val context = GlobalContext.getInstance()

    return File(File(context[DefaultProperty.DATA_DIR.key].toString()), database)
}


fun context(): SqlInsightContext {
    return SqlInsightContext.getInstance()
}

fun clearDatabase(databaseName: String) {
    "drop database if exists $databaseName".doSql()
}

fun createDatabase(databaseName: String) {
    "create database if not exists $databaseName".doSql()
}

fun createTable(databaseName: String, tableName: String) {
    createDatabase(databaseName)
    """create table if not exists $databaseName.$tableName(
        id int primary key auto_increment,
        name varchar not null
            )""".trimMargin().doSql()
}

fun insert(databaseName: String, tableName: String) {
    clearDatabase(databaseName)
    createTable(databaseName, tableName)
    """insert into aa.user (id,name) values
            (1,'a') ,(2,'b') ,(null,'c')
            ,(null,'b') ,(null,'c')
        """.trimMargin().doSql()
}

fun insertLarge(databaseName: String, tableName: String, length: Int) {
    clearDatabase(databaseName)
    createTable(databaseName, tableName)
    val values = (1..length).map {
        "(null,'${generatorRandomString(10)}')"
    }.joinToString(",")
    """insert into aa.user (id,name) values $values
        """.trimMargin().doSql()
}


fun generatorRandomString(length: Int): String {
    val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9') // 可选的字符集
    return (1..length)
        .map { Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("")
}
