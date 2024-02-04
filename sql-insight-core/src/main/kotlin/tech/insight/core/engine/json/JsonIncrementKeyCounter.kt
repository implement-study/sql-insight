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

import tech.insight.core.bean.Database
import tech.insight.core.bean.InsertRow
import tech.insight.core.bean.Table
import tech.insight.core.bean.value.ValueInt
import tech.insight.core.bean.value.ValueNull
import tech.insight.core.engine.AutoIncrementKeyCounter
import tech.insight.core.extension.slf4j
import tech.insight.core.extension.tree
import java.nio.file.Files
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * @author gongxuanzhangmelt@gmail.com
 */
object JsonIncrementKeyCounter : AutoIncrementKeyCounter {
    private val keyTable: MutableMap<String, MutableMap<String, AtomicLong>> = ConcurrentHashMap()
    private val log = slf4j<JsonIncrementKeyCounter>()

    /**
     * if auto increment column have value,fresh cache value.
     * else set a value to row
     *
     * @param row insert row
     */
    override fun dealAutoIncrement(row: InsertRow): Boolean {
        val autoColIndex: Int = row.table.ext.autoColIndex
        if (autoColIndex < 0) {
            return false
        }
        val databaseName = row.table.databaseName
        val atomicLong = loadMaxAutoIncrementKey(row.table)
        val autoIncrementValue = row.values[autoColIndex]
        if (autoIncrementValue is ValueNull) {
            row.values[autoColIndex] = ValueInt(atomicLong.incrementAndGet().toInt())
            return true
        }
        val insertValue = autoIncrementValue.source as Int
        if (insertValue > atomicLong.get()) {
            log.info(
                "database[{}],table[{}],auto increment col value set {}",
                databaseName,
                row.table.name,
                insertValue
            )
            atomicLong.set(insertValue.toLong())
        }
        return false
    }

    override fun reset(database: Database) {
        keyTable.computeIfAbsent(database.name) { ConcurrentHashMap() }.forEach { (_, al: AtomicLong) -> al.set(0) }
    }

    override fun reset(table: Table) {
        val atomicLong = loadMaxAutoIncrementKey(table)
        atomicLong.set(0)
    }

    private fun loadMaxAutoIncrementKey(table: Table): AtomicLong {
        val database: String = table.databaseName
        val tableName: String = table.name
        return keyTable.computeIfAbsent(database) { ConcurrentHashMap() }
            .computeIfAbsent(tableName) { loadFromDisk(table) }
    }

    private fun loadFromDisk(table: Table): AtomicLong {
        val jsonFile = JsonEngineSupport.getJsonFile(table)
        val allLines = Files.readAllLines(jsonFile.toPath())
        for (i in allLines.indices.reversed()) {
            if (allLines[i].isNotEmpty()) {
                val key = JsonEngineSupport.getJsonInsertRowPrimaryKey(table, allLines[i].tree())
                return AtomicLong(key.toLong())
            }
        }
        return AtomicLong()
    }
}
