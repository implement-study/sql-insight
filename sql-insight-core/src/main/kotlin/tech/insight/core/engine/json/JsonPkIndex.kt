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
package tech.insight.core.engine.json

import tech.insight.core.bean.*
import tech.insight.core.environment.Session
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author gongxuanzhangmelt@gmail.com
 */
class JsonPkIndex(private val table: Table) : Index {

    private lateinit var jsonFilePath: Path


    override fun rndInit() {
        jsonFilePath = JsonEngineSupport.getJsonFile(table).toPath()
    }

    override val id: Int
        get() = 1

    override fun belongTo(): Table {
        return table
    }

    override fun find(session: Session): Cursor {
        val reader = Files.newBufferedReader(jsonFilePath)
        return JsonCursor(reader, session, this)
    }

    override val name: String
        get() = "json"

    override fun insert(row: InsertRow) {
        throw UnsupportedOperationException("json engine index dont support insert")
    }

    override val file: File
        get() = jsonFilePath!!.toFile()

    override fun columns(): List<Column> {
        throw UnsupportedOperationException("json don't support")
    }
}
