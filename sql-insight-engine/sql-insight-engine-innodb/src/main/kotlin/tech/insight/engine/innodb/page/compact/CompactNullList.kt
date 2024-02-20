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
 */(var nullList: ByteArray) : ByteWrapper, PageObject {
    constructor(table: Table) : this(ByteArray(table.getExt().getNullableColCount() / java.lang.Byte.SIZE))

    /**
     * @param index start 0
     */
    fun isNull(index: Int): Boolean {
        val byteIndex = nullList.size - index / java.lang.Byte.SIZE - 1
        val bitMap = nullList[byteIndex]
        val mask = 1 shl index % java.lang.Byte.SIZE
        return mask and bitMap.toInt() == mask
    }

    fun setNull(index: Int) {
        val byteIndex = nullList.size - index / java.lang.Byte.SIZE - 1
        val mask = (1 shl index % java.lang.Byte.SIZE).toByte()
        nullList[byteIndex] = (nullList[byteIndex].toInt() and mask.toInt()).toByte()
    }

    fun toBytes(): ByteArray {
        return nullList
    }

    override fun length(): Int {
        return nullList.size
    }
}
