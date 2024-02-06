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

import com.fasterxml.jackson.databind.JsonNode
import tech.insight.core.bean.Column
import tech.insight.core.bean.DataType
import tech.insight.core.bean.ReadRow
import tech.insight.core.bean.Table
import tech.insight.core.bean.value.Value
import tech.insight.core.bean.value.ValueInt
import tech.insight.core.bean.value.ValueVarchar
import tech.insight.core.exception.DateTypeCastException
import java.io.File

/**
 * some static method support json engine
 *
 * @author gongxuanzhangmelt@gmail.com
 */
object JsonEngineSupport {
    /**
     * get a primary key from json.
     * the return type definitely int
     *
     * @param table table info
     * @param json  insert row
     * @return perhaps null
     */
    fun getJsonInsertRowPrimaryKey(table: Table, json: JsonNode): Int {
        val columnList: List<Column> = table.columnList
        val column: Column = columnList[table.ext.primaryKeyIndex]
        return json[column.name].intValue()
    }

    /**
     * get json data file
     *
     * @param table table
     * @return file perhaps not exists
     */
    fun getJsonFile(table: Table): File {
        return File(table.database.dbFolder, "${table.name}.json")
    }

    fun getPhysicRowFromJson(jsonNode: JsonNode, table: Table): ReadRow {
        val primaryKey: Column = table.columnList[table.ext.primaryKeyIndex]
        val id: Long = jsonNode[primaryKey.name].longValue()
        val valueList = table.columnList.map { wrapValue(it, jsonNode[it.name]) }.toList()
        val readRow = ReadRow(valueList, id)
        readRow.table = table
        return readRow
    }

    private fun wrapValue(column: Column, o: JsonNode): Value<*> {
        if (o.isNull) {
            return column.defaultValue
        }
        val type = column.dataType
        if (type === DataType.INT) {
            return ValueInt(o.intValue())
        }
        if (type == DataType.VARCHAR || type == DataType.CHAR) {
            return ValueVarchar(o.textValue())
        }
        throw DateTypeCastException(column.dataType.toString(), o.toString())
    }
}
