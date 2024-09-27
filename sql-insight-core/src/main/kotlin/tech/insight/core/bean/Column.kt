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

import tech.insight.buffer.SerializableObject
import tech.insight.buffer.byteBuf
import tech.insight.buffer.getAllBytes
import tech.insight.buffer.setBoolean
import tech.insight.buffer.writeLengthAndBytes
import tech.insight.buffer.writeLengthAndString
import tech.insight.core.bean.value.Value
import tech.insight.core.bean.value.ValueNull


/**
 * @author gongxuanzhangmelt@gmail.com
 */
class Column constructor(
    var name: String,
    val dataType: DataType,
    val length: Int,
    val autoIncrement: Boolean,
    val notNull: Boolean,
    val primaryKey: Boolean,
    val unique: Boolean,
    val hasDefault: Boolean,
    val defaultValue: Value<*>,
    val comment: String? = null,
    val nullListIndex: Int = -1, //  greater -1 means the column can be null
) : SQLBean, SerializableObject {
    val variable = dataType == DataType.VARCHAR

    override fun parent(): SQLBean? {
        return null
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


}
