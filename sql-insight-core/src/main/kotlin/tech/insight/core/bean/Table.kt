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
import tech.insight.buffer.SerializableObject
import tech.insight.buffer.byteBuf
import tech.insight.buffer.getAllBytes
import tech.insight.buffer.writeCollection
import tech.insight.buffer.writeLengthAndString
import tech.insight.core.bean.desc.TableExt
import tech.insight.core.engine.storage.StorageEngine
import tech.insight.core.exception.UnknownColumnException


/**
 * @author gongxuanzhangmelt@gmail.com
 */
class Table(
    val database: Database,
    val name: String,
    val columnList: MutableList<Column>,
    val indexList: MutableList<Index>,
    val engine: StorageEngine,
    val comment: String? = null
) : SQLBean, SerializableObject {
    @JsonIgnore
    val ext = TableExt(this)

    fun getColumnIndexByName(colName: String): Int {
        return ext.columnIndex[colName] ?: throw UnknownColumnException(colName)
    }

    fun getColumnByName(name: String): Column {
        return ext.columnMap[name] ?: throw UnknownColumnException(name)
    }

    override fun parent(): SQLBean {
        return database
    }

    override fun toBytes(): ByteArray {
        return byteBuf()
            .writeLengthAndString(name)
            .writeLengthAndString(database.name)
            .writeCollection(columnList)
            .writeLengthAndString(engine.name)
            .writeLengthAndString(comment)
            .getAllBytes()
    }

    override fun toString(): String {
        return "Table=[$name] ${columnList.size} columns"
    }

}



