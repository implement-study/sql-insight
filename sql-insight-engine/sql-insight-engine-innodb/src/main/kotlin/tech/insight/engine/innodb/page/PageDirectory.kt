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
package tech.insight.engine.innodb.page

import java.nio.ByteBuffer
import org.gongxuanzhang.easybyte.core.ByteWrapper

/**
 * a page has N * group.
 * each group has one to eight user records.
 * per slot represents the max data offset in page for group.
 * a slot take two bytes.
 * first slot only have infimum;
 * if the page just now initialized.
 * slot count is 2. contains infimum and supremum offset.
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 */
class PageDirectory private constructor(override val belongPage: InnoDbPage) : PageObject, ByteWrapper {

    lateinit var slots: ShortArray


    /**
     * page directory split.
     * @param splitSlotIndex split slot index. max slot is 0
     * @param newGroupMaxOffset new group max offset.  the new group max should be in [splitSlotIndex] before split
     */
    fun split(splitSlotIndex: Int, newGroupMaxOffset: Short) {
        slots = ShortArray(slots.size + 1) {
            if (it <= splitSlotIndex) {
                slots[it]
            } else if (it == splitSlotIndex + 1) {
                newGroupMaxOffset
            } else {
                slots[it - 1]
            }
        }
    }
    
    fun removeSlot(removedIndex : Int){
        slots = ShortArray(slots.size - 1) {
            if (it < removedIndex) {
                slots[it]
            } else {
                slots[it + 1]
            }
        }
    }

    override fun length(): Int {
        return slots.size * Short.SIZE_BYTES
    }

    override fun toBytes(): ByteArray {
        val buffer = ByteBuffer.allocate(length())
        for (slot in slots) {
            buffer.putShort(slot)
        }
        return buffer.array()
    }

    /**
     *
     */
    fun slotCount(): Int {
        return slots.size
    }

    fun indexSlot(index: Int): Short {
        return slots[index]
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PageDirectory) return false

        if (!slots.contentEquals(other.slots)) return false

        return true
    }

    override fun hashCode(): Int {
        return slots.contentHashCode()
    }

    override fun toString(): String {
        return "PageDirectory(slot size: ${slots.size})"
    }


    companion object PageDirectoryFactory {

        /**
         * non params constructor create a contains infimum and supremum offset slot.
         */
        fun create(belongPage: InnoDbPage) = PageDirectory(belongPage).apply {
            slots = shortArrayOf(ConstantSize.SUPREMUM.offset().toShort(), ConstantSize.INFIMUM.offset().toShort())
        }

        fun wrap(slots: ShortArray, belongPage: InnoDbPage) = PageDirectory(belongPage).apply {
            require(slots.size >= 2) { "slots size must be greater than 2" }
            require(slots[0] == ConstantSize.SUPREMUM.offset().toShort()) { "first slot must be supremum" }
            require(slots[slots.size - 1] == ConstantSize.INFIMUM.offset().toShort()) { "last slot must be infimum" }
            this.slots = slots
        }


    }

}
