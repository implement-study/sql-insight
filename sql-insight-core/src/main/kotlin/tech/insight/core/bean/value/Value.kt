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
package tech.insight.core.bean.value


/**
 * base value
 * [T] is value type.
 * @author gongxuanzhangmelt@gmail.com
 */
interface Value<T> : Comparable<Value<T>> {
    /**
     * length for value
     *
     * @return byte array length
     */
    val length: Int

    /**
     * the value is dynamic
     *
     * @return true is dynamic
     */
    val isDynamic: Boolean

    /**
     * value source
     */
    val source: T

    /**
     * to byte array
     *
     * @return byte array equals getLength()
     */
    fun toBytes(): ByteArray
}

class ValueChar(value: String, length: Int) : Value<String> {
    override val length: Int
    override val isDynamic = false
    override val source: String

    init {
        check(value.length <= length) { "$value length more than $length" }
        this.source = value.padEnd(length, ' ')
        this.length = length
    }


    override fun toBytes(): ByteArray {
        return source.toByteArray()
    }

    override fun compareTo(other: Value<String>): Int {
        return this.source.compareTo(other.source)
    }

}

class ValueVarchar(override val source: String) : Value<String> {
    override val length: Int
        get() {
            return toBytes().size
        }
    override val isDynamic = true


    override fun toBytes(): ByteArray {
        return source.toByteArray()
    }

    override fun compareTo(other: Value<String>): Int {
        return this.source.compareTo(other.source)
    }
}
