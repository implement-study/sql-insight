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

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import tech.insight.core.bean.DataType
import tech.insight.core.bean.InsertRow
import tech.insight.core.bean.Row
import tech.insight.core.bean.Table
import tech.insight.core.command.UpdateCommand
import tech.insight.core.engine.storage.StorageEngine
import tech.insight.core.event.DropDatabaseEvent
import tech.insight.core.event.DropTableEvent
import tech.insight.core.event.InsightEvent
import tech.insight.core.event.MultipleEventListener
import tech.insight.core.exception.CreateTableException
import tech.insight.core.exception.InsertException
import tech.insight.core.exception.RuntimeIoException
import tech.insight.core.extension.json
import tech.insight.core.extension.slf4j
import tech.insight.core.extension.tree
import tech.insight.core.result.MessageResult
import tech.insight.core.result.ResultInterface
import java.io.File
import java.io.IOException
import java.nio.file.Files

/**
 * @author gongxuanzhangmelt@gmail.com
 */
class JsonEngine : StorageEngine, MultipleEventListener {
    private val log = slf4j<JsonEngine>()

    companion object {
        const val MIN_PRIMARY_KEY = 1
        const val MAX_PRIMARY_KEY = 10000
    }

    private val counter = JsonIncrementKeyCounter
    override val name: String
        get() = "json"

    override fun tableExtensions(): List<String> {
        return listOf("json")
    }

    override fun openTable(table: Table) {
        if (table.indexList.isNotEmpty()) {
            return
        }
        val jsonPkIndex = JsonPkIndex(table)
        table.indexList.add(jsonPkIndex)
    }

    override fun createTable(table: Table): ResultInterface {
        val dbFolder: File = table.database.dbFolder
        if (table.ext.primaryKeyIndex == -1) {
            throw CreateTableException("engine json table must have a primary key")
        }
        if (table.columnList[table.ext.primaryKeyIndex].dataType != DataType.INT) {
            throw CreateTableException("engine json table primary key must int ")
        }
        val jsonFile = File(dbFolder, "${table.name}.json")
        if (!jsonFile.createNewFile()) {
            log.warn("create file {} fail", jsonFile.getName())
        }
        val initContent: MutableList<String> = ArrayList()
        for (i in 0 until MAX_PRIMARY_KEY) {
            initContent.add("")
        }
        Files.write(jsonFile.toPath(), initContent)
        log.info("write {} json to {}", initContent.size, jsonFile.toPath().toAbsolutePath())
        return MessageResult("success create table ${table.name}")
    }

    override fun truncateTable(table: Table): ResultInterface {
        val jsonFile = JsonEngineSupport.getJsonFile(table)
        val initContent: MutableList<String> = ArrayList()
        for (i in 0 until MAX_PRIMARY_KEY) {
            initContent.add("")
        }
        Files.write(jsonFile.toPath(), initContent)
        counter.reset(table)
        return MessageResult("success truncate table ${table.name}")
    }

    override fun insertRow(row: InsertRow) {
        counter.dealAutoIncrement(row)
        val jsonObject = fullAllColumnRow(row)
        val jsonFile = JsonEngineSupport.getJsonFile(row.belongTo())
        val insertPrimaryKey = JsonEngineSupport.getJsonInsertRowPrimaryKey(row.belongTo(), jsonObject)
        if (MAX_PRIMARY_KEY < insertPrimaryKey || insertPrimaryKey < MIN_PRIMARY_KEY) {
            throw InsertException("engine json primary key must between $MIN_PRIMARY_KEY and $MAX_PRIMARY_KEY")
        }
        try {
            val lines = Files.readAllLines(jsonFile.toPath())
            val currentLine = lines[insertPrimaryKey]
            if (currentLine.isNotEmpty()) {
                throw InsertException(String.format("Duplicate entry '%s' for key 'PRIMARY'", insertPrimaryKey))
            }
            lines[insertPrimaryKey] = jsonObject.toString()
            log.info("insert {} to table [{}] ", jsonObject, row.table.name)
            Files.write(jsonFile.toPath(), lines)
        } catch (e: IOException) {
            throw RuntimeIoException(e)
        }
    }

    override fun update(oldRow: Row, update: UpdateCommand) {
        val jsonFile = JsonEngineSupport.getJsonFile(update.table)
        val rowId = oldRow.rowId.toInt()
        try {
            val lines = Files.readAllLines(jsonFile.toPath())
            val line = lines[rowId]
            val jsonNode = line.tree()
            update.updateField.forEach { (colName, expression) ->
                val expressionValue = expression.getExpressionValue(oldRow)
                jsonNode.put(colName, expressionValue.source)
                val newLine = jsonNode.json()
                lines[rowId] = newLine
                log.info("update {} to {} ", line, newLine)
            }
            Files.write(jsonFile.toPath(), lines)
        } catch (e: IOException) {
            throw RuntimeIoException(e)
        }
    }

    override fun delete(deletedRow: Row) {
        val rowId: Long = deletedRow.rowId
        val jsonFile = JsonEngineSupport.getJsonFile(deletedRow.belongTo())
        try {
            val lines = Files.readAllLines(jsonFile.toPath())
            lines[rowId.toInt()] = ""
            Files.write(jsonFile.toPath(), lines)
        } catch (e: IOException) {
            throw RuntimeIoException(e)
        }
    }

    override fun refresh(table: Table) {
        log.warn("The json engine dose not refresh manually because in update or delete already refresh")
    }

    private fun fullAllColumnRow(row: InsertRow): ObjectNode {
        val table: Table = row.table
        val jsonObject = jacksonObjectMapper().createObjectNode()
        table.columnList.forEach { col -> jsonObject.putNull(col.name) }
        val insertColumns = row.insertColumns
        val values = row.values
        for (i in values.indices) {
            jsonObject.put(insertColumns[i].name, values[i].source)
        }
        return jsonObject
    }

    override fun onEvent(event: InsightEvent) {
        if (event is DropTableEvent) {
            counter.reset(event.table)
        } else if (event is DropDatabaseEvent) {
            counter.reset(event.database)
        }
    }

    override fun listenEvent(): List<Class<out InsightEvent?>> {
        return listOf(DropTableEvent::class.java, DropDatabaseEvent::class.java)
    }


    /**
     * support put object
     */
    private fun ObjectNode.put(key: String, any: Any?) {
        if (any == null) {
            this.putNull(key)
            return
        }
        if (any is String) {
            this.put(key, any)
            return
        }
        if (any is Int) {
            this.put(key, any)
            return
        }
    }


}
