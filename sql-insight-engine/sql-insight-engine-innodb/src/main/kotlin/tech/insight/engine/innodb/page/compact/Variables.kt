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
package tech.insight.engine.innodb.page.compact

import org.gongxuanzhang.easybyte.core.ByteWrapper
import tech.insight.engine.innodb.page.PageObject

/**
 * variable data type like varchar
 *
 * @author gongxuanzhang
 */
class Variables : ByteWrapper, PageObject, Iterable<Byte> {
    var varBytes: ByteArray

    constructor() {
        varBytes = ByteArray(0)
    }

    constructor(varBytes: ByteArray) {
        this.varBytes = varBytes
    }

    fun addVariableLength(length: Byte) {
        if (varBytes.isEmpty()) {
            varBytes = byteArrayOf(length)
            return
        }
        val newBytes = ByteArray(varBytes.size + 1)
        System.arraycopy(varBytes, 0, newBytes, 1, varBytes.size)
        newBytes[0] = length
        varBytes = newBytes
    }

    /**
     * all variable column length
     */
    fun variableLength(): Int {
        var sumLength = 0
        for (varByte in varBytes) {
            sumLength += varByte.toInt()
        }
        return sumLength
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
}
