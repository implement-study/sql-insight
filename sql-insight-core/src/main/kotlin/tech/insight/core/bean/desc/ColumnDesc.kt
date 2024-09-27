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
import tech.insight.buffer.isOne
import tech.insight.buffer.readLength
import tech.insight.buffer.readLengthAndString
import tech.insight.buffer.setBoolean
import tech.insight.buffer.wrappedBuf
import tech.insight.buffer.writeLengthAndBytes
import tech.insight.buffer.writeLengthAndString
import tech.insight.core.bean.Column
import tech.insight.core.bean.DataType
import tech.insight.core.bean.Description
import tech.insight.core.bean.value.Value
import tech.insight.core.bean.value.ValueNull


/**
 * @author gongxuanzhangmelt@gmail.com
 */
class ColumnDesc : Description<Column> {
    var name: String? = null
    var dataType: DataType? = null
    var length: Int? = null
    var autoIncrement = false
    var notNull = false
    var primaryKey = false
    var unique = false
    var defaultValue: Value<*>? = null
    var comment: String? = null


    override fun checkMySelf() {
        check(name != null) { "col name can't be null" }
        check(dataType != null) { "col dataType can't be null" }
        if (length == null) {
            length = dataType!!.defaultLength
        }
        check(length!! > 0 && length!! <= UShort.MAX_VALUE.toInt()) { "col length must between 0 and ${UShort.MAX_VALUE}" }
        if (this.primaryKey) {
            check(defaultValue == null) { "Invalid default value for '$name'" }
        }
        if (autoIncrement) {
            check(dataType == DataType.INT) { "auto increment column must be int " }
        }
    }

    override fun build(): Column {
        checkMySelf()
        return Column(
            name!!,
            dataType!!,
            length!!,
            autoIncrement,
            notNull,
            primaryKey,
            unique,
            defaultValue != null && defaultValue !is ValueNull,
            defaultValue ?: ValueNull,
            comment
        )
    }

    override fun toBytes(): ByteArray {
        val flag = 0.toByte()
            .setBoolean(0, autoIncrement)
            .setBoolean(1, notNull)
            .setBoolean(2, primaryKey)
            .setBoolean(3, unique)
        val buffer = byteBuf()
            .writeByte(flag.toInt())
            .writeLengthAndString(name)
            .writeBytes(dataType!!.toBytes())
            .writeInt(length!!)
            .writeLengthAndString(comment)
        if (defaultValue == null || defaultValue is ValueNull) {
            buffer.writeInt(-1)
        } else {
            buffer.writeLengthAndBytes(defaultValue!!.toBytes())
        }
        return buffer.getAllBytes()
    }

    companion object {
        val reader = ObjectReader { bytes: ByteArray ->
            val columnDesc = ColumnDesc()
            val buf = wrappedBuf(bytes)
            buf.readByte().apply {
                columnDesc.autoIncrement = this.isOne(0)
                columnDesc.notNull = this.isOne(1)
                columnDesc.primaryKey = this.isOne(2)
                columnDesc.unique = this.isOne(3)
            }
            columnDesc.name =
                buf.readLengthAndString() ?: throw IllegalArgumentException("column name can't be null")
            columnDesc.dataType = DataType.entries[buf.readByte().toInt()]
            columnDesc.length = buf.readInt()
            columnDesc.comment = buf.readLengthAndString()
            val length = buf.readInt()
            if (length == -1) {
                columnDesc.defaultValue = ValueNull
                return@ObjectReader columnDesc
            }
            val valueBytes = buf.readLength(length)
            columnDesc.defaultValue = columnDesc.dataType!!.reader.readObject(valueBytes)
            return@ObjectReader columnDesc
        }
    }
}


