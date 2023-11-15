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
import java.io.File


fun databaseFile(database: String): File {
    val context = GlobalContext.getInstance()

    return File(File(context[DefaultProperty.DATA_DIR.key].toString()), database)
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
        id int primary key,
        name varchar not null
            )""".trimMargin().doSql()
}
