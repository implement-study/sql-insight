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

import tech.insight.buffer.ObjectReader
import tech.insight.buffer.SerializableObject
import tech.insight.buffer.byteBuf
import tech.insight.buffer.getAllBytes
import tech.insight.buffer.isOne
import tech.insight.buffer.readLength
import tech.insight.buffer.readLengthAndString
import tech.insight.buffer.setBoolean
import tech.insight.buffer.wrappedBuf
import tech.insight.buffer.writeLengthAndBytes
import tech.insight.buffer.writeLengthAndString
import tech.insight.core.bean.value.Value
import tech.insight.core.bean.value.ValueNull


/**
 * @author gongxuanzhangmelt@gmail.com
 */
class Column : SQLBean, SerializableObject {
    lateinit var name: String
    lateinit var dataType: DataType
    var length: Int = -1
    var autoIncrement = false
    var notNull = false
    var primaryKey = false
    var unique = false
    var hasDefault = false
    var defaultValue: Value<*> = ValueNull
    var comment: String? = null
    var variable = false
    var nullListIndex = -1


    override fun checkMyself() {
        check(length > 0) { "col length must gather than 0" }
        check(length <= UShort.MAX_VALUE.toInt()) { "col length must less than ${UShort.MAX_VALUE}" }
        if (this.primaryKey) {
            check(defaultValue is ValueNull) { "primary key can't have default value" }
        }
    }

    override fun toBytes(): ByteArray {
        val flag = 0.toByte()
            .setBoolean(0, autoIncrement)
            .setBoolean(1, notNull)
            .setBoolean(2, primaryKey)
            .setBoolean(3, unique)
            .setBoolean(4, variable)
            .setBoolean(5, hasDefault)
        val buffer = byteBuf()
            .writeByte(flag.toInt())
            .writeLengthAndString(name)
            .writeBytes(dataType.toBytes())
            .writeInt(length)
            .writeLengthAndString(comment)
        if (defaultValue is ValueNull) {
            buffer.writeInt(-1)
        } else {
            buffer.writeLengthAndBytes(defaultValue.toBytes())
        }
        return buffer.getAllBytes()
    }


    override fun toString(): String {
        return "Column($name $dataType $length)"
    }

    companion object ColumnReader : ObjectReader<Column> {

        override fun readObject(bytes: ByteArray): Column {
            val column = Column()
            val buf = wrappedBuf(bytes)
            buf.readByte().apply {
                column.autoIncrement = this.isOne(0)
                column.notNull = this.isOne(1)
                column.primaryKey = this.isOne(2)
                column.unique = this.isOne(3)
                column.variable = this.isOne(4)
            }
            column.name = buf.readLengthAndString() ?: throw IllegalArgumentException("column name can't be null")
            column.dataType = DataType.entries[buf.readByte().toInt()]
            column.length = buf.readInt()
            column.comment = buf.readLengthAndString()
            val length = buf.readInt()
            if (length == -1) {
                column.defaultValue = ValueNull
                return column
            }
            val valueBytes = buf.readLength(length)
            column.defaultValue = column.dataType.reader.readObject(valueBytes)
            return column
        }
    }
}
