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
import tech.insight.core.bean.Table
import tech.insight.engine.innodb.page.PageObject

/**
 * contains byte array.
 * per byte bit represent wh column value
 *
 * @author gongxuanzhang
 */
class CompactNullList
/**
 * the byte array is origin byte in page.
 * begin with right.
 * nullList length maybe 0
 */(private var nullList: ByteArray) : ByteWrapper, PageObject {
    constructor(table: Table) : this(ByteArray(table.ext.nullableColCount / Byte.SIZE_BITS))

    /**
     * @param index start 0
     */
    fun isNull(index: Int): Boolean {
        val byteIndex = nullList.size - index / Byte.SIZE_BITS - 1
        val bitMap = nullList[byteIndex]
        val mask = 1 shl index % Byte.SIZE_BITS
        return mask and bitMap.toInt() == mask
    }

    fun setNull(index: Int) {
        val byteIndex = nullList.size - index / Byte.SIZE_BITS - 1
        val mask = (1 shl index % Byte.SIZE_BITS).toByte()
        nullList[byteIndex] = (nullList[byteIndex].toInt() and mask.toInt()).toByte()
    }

    override fun toBytes(): ByteArray {
        return nullList
    }

    override fun length(): Int {
        return nullList.size
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CompactNullList) return false

        if (!nullList.contentEquals(other.nullList)) return false

        return true
    }

    override fun hashCode(): Int {
        return nullList.contentHashCode()
    }


}
