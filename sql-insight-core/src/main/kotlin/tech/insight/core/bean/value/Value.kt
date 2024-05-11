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
package tech.insight.core.bean.value

import tech.insight.core.extension.toByteArray


/**
 * base value
 * [T] is value type.
 * @author gongxuanzhangmelt@gmail.com
 */
sealed interface Value<T> : Comparable<Value<*>> {
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

    operator fun plus(other: Value<*>): Value<T> {
        throw UnsupportedOperationException("${javaClass.name} not support plus")
    }

    operator fun minus(other: Value<*>): Value<T> {
        throw UnsupportedOperationException("${javaClass.name} not support minus")
    }

    operator fun times(other: Value<*>): Value<T> {
        throw UnsupportedOperationException("${javaClass.name} not support times")
    }

    operator fun div(other: Value<*>): Value<T> {
        throw UnsupportedOperationException("${javaClass.name} not support div")
    }

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


    override fun toString(): String {
        return source
    }

    override fun toBytes(): ByteArray {
        return source.toByteArray()
    }

    override fun plus(other: Value<*>): Value<String> {
        return ValueVarchar("$source${other.source}")
    }

    override fun compareTo(other: Value<*>): Int {
        return when (other) {
            is ValueBoolean -> this.source.compareTo(if (other.source) "1" else "0")
            is ValueNull -> 1
            else -> this.source.compareTo(other.source.toString())
        }
    }

}

data class ValueVarchar(override val source: String) : Value<String> {
    override val length: Int
        get() {
            return toBytes().size
        }
    override val isDynamic = true


    override fun toBytes(): ByteArray {
        return source.toByteArray()
    }

    override fun compareTo(other: Value<*>): Int {
        return when (other) {
            is ValueBoolean -> this.source.compareTo(if (other.source) "1" else "0")
            is ValueNull -> 1
            else -> this.source.compareTo(other.source.toString())
        }
    }

    override fun plus(other: Value<*>): Value<String> {
        return ValueVarchar("$source${other.source}")
    }

    override fun toString(): String {
        return this.source
    }
}

data class ValueInt(override val source: Int) : Value<Int> {
    override val length = Int.SIZE_BYTES
    override val isDynamic = false


    override fun toBytes(): ByteArray {
        return source.toByteArray()
    }

    override fun toString(): String {
        return source.toString()
    }

    override fun compareTo(other: Value<*>): Int {
        return when (other) {
            is ValueBoolean -> this.source.compareTo(if (other.source) 1 else 0)
            is ValueNull -> 1
            is ValueInt -> this.source.compareTo(other.source)
            is ValueChar -> this.source.toString().compareTo(other.source)
            is ValueVarchar -> this.source.toString().compareTo(other.source)
        }
    }

    override operator fun plus(other: Value<*>): Value<Int> {
        require(other is ValueInt) { "number can't plus a ${other.javaClass}" }
        return ValueInt(this.source + other.source)
    }

    override operator fun minus(other: Value<*>): Value<Int> {
        require(other is ValueInt) { "number can't plus a ${other.javaClass}" }
        return ValueInt(this.source - other.source)
    }

    override operator fun times(other: Value<*>): Value<Int> {
        require(other is ValueInt) { "number can't plus a ${other.javaClass}" }
        return ValueInt(this.source * other.source)
    }

    override operator fun div(other: Value<*>): Value<Int> {
        require(other is ValueInt) { "number can't plus a ${other.javaClass}" }
        return ValueInt(this.source / other.source)
    }
}

open class ValueBoolean(override val source: Boolean) : Value<Boolean> {
    override val length = Byte.SIZE_BYTES
    override val isDynamic = false


    override fun toBytes(): ByteArray {
        return source.toByteArray()
    }


    override fun compareTo(other: Value<*>): Int {
        return when (other) {
            is ValueBoolean -> this.source.compareTo(other.source)
            is ValueNull -> 1
            is ValueInt -> if (this.source) 1 else 0.compareTo(other.source)
            is ValueChar -> this.source.toString().compareTo(other.source)
            is ValueVarchar -> this.source.toString().compareTo(other.source)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ValueBoolean

        return source == other.source
    }

    override fun hashCode(): Int {
        var result = source.hashCode()
        result = 31 * result + length
        result = 31 * result + isDynamic.hashCode()
        return result
    }


}

object ValueTrue : ValueBoolean(true)
object ValueFalse : ValueBoolean(false)


data object ValueNull : Value<Unit> {
    override val source: Unit = Unit
    override val length = 0
    override val isDynamic = false


    override fun toBytes(): ByteArray {
        return ByteArray(0)
    }

    override fun compareTo(other: Value<*>): Int {
        return -1
    }

    override fun toString(): String {
        return "null"
    }


}


