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
package tech.insight.core.bean

import com.fasterxml.jackson.annotation.JsonIgnore
import tech.insight.buffer.ObjectReader
import tech.insight.buffer.SerializableObject
import tech.insight.buffer.byteBuf
import tech.insight.buffer.getAllBytes
import tech.insight.buffer.readCollection
import tech.insight.buffer.readLengthAndString
import tech.insight.buffer.wrappedBuf
import tech.insight.buffer.writeCollection
import tech.insight.buffer.writeLengthAndString
import tech.insight.core.engine.storage.StorageEngine
import tech.insight.core.environment.DatabaseManager
import tech.insight.core.environment.EngineManager
import tech.insight.core.exception.UnknownColumnException


/**
 * @author gongxuanzhangmelt@gmail.com
 */
class Table : SQLBean, SerializableObject {
    lateinit var database: Database
    lateinit var name: String
    var columnList: MutableList<Column> = mutableListOf()
    var indexList: MutableList<Index> = mutableListOf()
    lateinit var engine: StorageEngine
    var comment: String? = null
    val databaseName: String
        @JsonIgnore
        get() {
            return database.name
        }

    /**
     * support operator
     */
    val ext = TableExt()
    fun getColumnIndexByName(colName: String): Int {
        return ext.columnIndex[colName] ?: throw UnknownColumnException(colName)
    }

    fun getColumnByName(name: String): Column {
        return ext.columnMap[name] ?: throw UnknownColumnException(name)
    }


    override fun checkMyself() {
        //   TODO  move to table factory? 
        this.columnList.forEachIndexed { index, col ->
            col.checkMyself()
            ext.columnMap[col.name] = col
            ext.columnIndex[col.name] = index
            if (col.primaryKey) {
                this.ext.primaryKeyIndex = index
                this.ext.primaryKeyName = col.name
            }
            if (col.autoIncrement) {
                require(ext.autoColIndex == -1) { "auto increment column can have at most one" }
                ext.autoColIndex = index
            }
            if (col.notNull) {
                ext.notNullIndex.add(index)
            } else {
                col.nullListIndex = ext.nullableColCount
                ext.nullableColCount++
            }
            if (col.variable) {
                ext.variableIndex.add(index)
            }
        }
    }

    override fun toBytes(): ByteArray {
        return byteBuf()
            .writeLengthAndString(name)
            .writeLengthAndString(databaseName)
            .writeCollection(columnList)
            .writeLengthAndString(engine.name)
            .writeLengthAndString(comment)
            .getAllBytes()
    }

    override fun toString(): String {
        return "Table=[$name] ${columnList.size} columns"
    }

    class TableExt {
        val columnMap: MutableMap<String, Column> = HashMap()
        val columnIndex: MutableMap<String, Int> = HashMap()

        /**
         * not null column index list
         */
        val notNullIndex: MutableList<Int> = ArrayList()
        val variableIndex: MutableList<Int> = ArrayList()
        var autoColIndex = -1
        var primaryKeyIndex = -1
        var nullableColCount = 0
        var primaryKeyName: String? = null
    }

    companion object : ObjectReader<Table> {

        override fun readObject(bytes: ByteArray): Table {
            val table = Table()
            val buf = wrappedBuf(bytes)
            table.name = buf.readLengthAndString() ?: throw IllegalArgumentException("table name can't be null")
            table.database = DatabaseManager.require(buf.readLengthAndString()!!)
            table.columnList = buf.readCollection {
                Column.readObject(it)
            }.toMutableList()
            table.engine = EngineManager.selectEngine(buf.readLengthAndString())
            table.comment = buf.readLengthAndString()
            return table
        }

    }

}



