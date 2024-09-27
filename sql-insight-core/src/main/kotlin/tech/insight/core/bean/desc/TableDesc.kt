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
package tech.insight.core.bean.desc

import tech.insight.buffer.ObjectReader
import tech.insight.buffer.byteBuf
import tech.insight.buffer.getAllBytes
import tech.insight.buffer.readCollection
import tech.insight.buffer.readLengthAndString
import tech.insight.buffer.wrappedBuf
import tech.insight.buffer.writeCollection
import tech.insight.buffer.writeLengthAndString
import tech.insight.core.bean.Description
import tech.insight.core.bean.Table
import tech.insight.core.environment.DatabaseManager
import tech.insight.core.environment.EngineManager


/**
 * @author gongxuanzhangmelt@gmail.com
 */
class TableDesc : Description<Table> {
    var name: String? = null
    var columnList: MutableList<ColumnDesc> = mutableListOf()
    var engine: String? = null
    var comment: String? = null
    var databaseName: String? = null


    override fun checkMySelf() {
        check(name != null) { "table name is null" }
        this.columnList.forEach { it.checkMySelf() }
    }

    override fun build(): Table {
        return Table(
            database = DatabaseManager.require(databaseName!!),
            name = name!!,
            columnList = columnList.map { it.build() },
            indexList = emptyList(),
            engine = EngineManager.selectEngine(engine!!),
            comment = comment
        )
    }

    override fun toBytes(): ByteArray {
        return byteBuf()
            .writeLengthAndString(name)
            .writeLengthAndString(databaseName)
            .writeCollection(columnList)
            .writeLengthAndString(engine)
            .writeLengthAndString(comment)
            .getAllBytes()
    }

    companion object {

        val reader = ObjectReader { bytes: ByteArray ->
            val tableDesc = TableDesc()
            val buf = wrappedBuf(bytes)
            tableDesc.name = buf.readLengthAndString() ?: throw IllegalArgumentException("table name can't be null")
            tableDesc.databaseName = buf.readLengthAndString()
            tableDesc.columnList = buf.readCollection {
                ColumnDesc.reader.readObject(it)
            }.toMutableList()
            tableDesc.engine = buf.readLengthAndString()
            tableDesc.comment = buf.readLengthAndString()
            tableDesc
        }
    }


}



