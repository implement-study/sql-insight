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

import com.alibaba.fastjson2.JSONObject
import com.google.common.collect.HashBasedTable
import com.google.common.collect.Tables
import tech.insight.core.bean.Database
import tech.insight.core.bean.Table
import java.io.IOException
import java.nio.file.Files
import java.util.*
import java.util.concurrent.atomic.AtomicLong

/**
 * @author gongxuanzhangmelt@gmail.com
 */
@Slf4j
class JsonIncrementKeyCounter : AutoIncrementKeyCounter {
    private val keyTable = Tables.synchronizedTable(HashBasedTable.create<String, String, AtomicLong?>())

    /**
     * if auto increment column have value,fresh cache value.
     * else set a value to row
     *
     * @param row insert row
     */
    fun dealAutoIncrement(row: InsertRow): Boolean {
        val autoColIndex: Int = row.getTable().getExt().getAutoColIndex()
        if (autoColIndex < 0) {
            return false
        }
        val databaseName: String = row.getTable().getDatabase().getName()
        val atomicLong = loadMaxAutoIncrementKey(row.getTable())
        val autoIncrementValue: Value = row.getValues().get(autoColIndex)
        if (autoIncrementValue.getSource() == null) {
            row.getValues().set(autoColIndex, ValueInt(atomicLong!!.incrementAndGet().toInt()))
            return true
        }
        val insertValue = autoIncrementValue.getSource() as Int
        if (insertValue > atomicLong!!.get()) {
            log.info(
                "database[{}],table[{}],auto increment col value set {}",
                databaseName,
                row.getTable().getName(),
                insertValue
            )
            atomicLong.set(insertValue.toLong())
        }
        return false
    }

    fun reset(database: Database) {
        val row = keyTable.row(database.getName())
        row.forEach { (tableName: String?, al: AtomicLong?) -> al!!.set(0) }
    }

    fun reset(table: Table) {
        val atomicLong = loadMaxAutoIncrementKey(table)
        atomicLong!!.set(0)
    }

    private fun loadMaxAutoIncrementKey(table: Table): AtomicLong? {
        val database: String = table.getDatabase().getName()
        val tableName: String = table.getName()
        var atomicLong = keyTable[database, tableName]
        if (atomicLong == null) {
            synchronized(keyTable) {
                if (keyTable[database, tableName] == null) {
                    atomicLong = loadFromDisk(table)
                    keyTable.put(database, tableName, atomicLong)
                }
            }
        }
        return Objects.requireNonNull(atomicLong)
    }

    private fun loadFromDisk(table: Table): AtomicLong {
        val jsonFile = JsonEngineSupport.getJsonFile(table)
        return try {
            val allLines = Files.readAllLines(jsonFile!!.toPath())
            for (i in allLines.indices.reversed()) {
                if (!allLines[i].isEmpty()) {
                    val key = JsonEngineSupport.getJsonInsertRowPrimaryKey(table, JSONObject.parse(allLines[i]))
                    return AtomicLong(key.toLong())
                }
            }
            AtomicLong()
        } catch (e: IOException) {
            throw RuntimeIoException(e)
        }
    }
}
