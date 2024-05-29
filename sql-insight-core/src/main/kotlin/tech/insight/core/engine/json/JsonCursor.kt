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
package tech.insight.core.engine.json

import java.io.BufferedReader
import tech.insight.core.bean.Cursor
import tech.insight.core.bean.Row
import tech.insight.core.extension.tree

/**
 * @author gongxuanzhangmelt@gmail.com
 */
class JsonCursor(private val reader: BufferedReader, private val index: JsonPkIndex) : Cursor {
    private var current: Row? = null
    private var count = 0

    override fun hasNext(): Boolean {
        if (current != null) {
            return true
        }
        while (true) {
            try {
                val line = reader.readLine() ?: return false
                if (line.isEmpty()) {
                    continue
                }
                current = JsonEngineSupport.getPhysicRowFromJson(line.tree(), index.belongTo())
                return true
            } catch (e: Exception) {
                throw e
            }
        }
    }


    override fun next(): Row {
        if (current == null) {
            throw NoSuchElementException()
        }
        val result: Row = current!!
        current = null
        return result
    }

    override fun close() {
        reader.close()
    }
}
