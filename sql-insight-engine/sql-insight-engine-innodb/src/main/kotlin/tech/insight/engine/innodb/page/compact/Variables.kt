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
package tech.insight.engine.innodb.page.compact

import org.gongxuanzhang.easybyte.core.ByteWrapper
import tech.insight.engine.innodb.core.Lengthable

/**
 * variable data type like varchar
 *
 * @author gongxuanzhang
 */
class Variables private constructor() : ByteWrapper, Lengthable, Iterable<Byte> {

    lateinit var varBytes: ByteArray

    /**
     * append variable length
     * @param length column value length
     */
    fun appendVariableLength(length: UByte) {
        if (varBytes.isEmpty()) {
            varBytes = byteArrayOf(length.toByte())
            return
        }
        val newBytes = ByteArray(varBytes.size + 1)
        System.arraycopy(varBytes, 0, newBytes, 1, varBytes.size)
        newBytes[0] = length.toByte()
        varBytes = newBytes
    }

    /**
     * all variable column length
     */
    fun variableLength(): Int {
        var sumLength = 0
        for (varByte in varBytes) {
            sumLength += varByte.toUByte().toInt()
        }
        return sumLength
    }


    /**
     * @param indexInColumn col in column index exclude null list
     */
    fun getVariableLength(indexInColumn: Int): Byte {
        return varBytes[varBytes.size - indexInColumn - 1]
    }

    override fun toBytes(): ByteArray {
        return varBytes
    }

    override fun length(): Int {
        return varBytes.size
    }

    override fun iterator(): Iterator<Byte> {
        return ReIter()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Variables) return false

        if (!varBytes.contentEquals(other.varBytes)) return false

        return true
    }

    override fun hashCode(): Int {
        return varBytes.contentHashCode()
    }


    inner class ReIter : Iterator<Byte> {
        private var cursor = varBytes.size - 1
        override fun hasNext(): Boolean {
            return cursor >= 0
        }

        override fun next(): Byte {
            if (cursor < 0) {
                throw NoSuchElementException()
            }
            val i = cursor
            cursor--
            return varBytes[i]
        }
    }

    companion object VariablesFactory {

        fun create() = Variables().apply { this.varBytes = ByteArray(0) }

        fun wrap(varBytes: ByteArray) = Variables().apply { this.varBytes = varBytes }
    }
}
