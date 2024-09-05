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

import io.netty.buffer.ByteBuf
import java.nio.ByteBuffer

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
class PageDirectory(override val belongPage: InnoDbPage) : PageObject {

    var source: ByteBuf = pageBuff()


    /**
     * order AES in order to support binary search
     */
    var slots = ShortArray(belongPage.pageHeader.slotCount) {
        val offset: Int = ConstantSize.FILE_TRAILER.offset - (it + 1) * Short.SIZE_BYTES
        source.getShort(offset)
    }

    /**
     * page directory split.
     * @param splitSlotIndex split slot index. min slot is 0
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


    /**
     * @param removedIndex min slot is 0
     */
    fun removeSlot(removedIndex: Int) {
        require(removedIndex > 0 && removedIndex < slots.size - 1) {
            "removed index must in 1...${slots.size - 1} infimum and supremum can't remove"
        }
        this.source.setShort(0, 0)
        slots = ShortArray(slots.size - 1) {
            if (it < removedIndex) {
                slots[it]
            } else {
                val newOffset = slots[it + 1]
                this.source.setShort(this.source.capacity() - (it + 1) * Short.SIZE_BYTES, newOffset.toInt())
                newOffset
            }
        }
        this.belongPage.pageHeader.slotCount--
    }

    fun replace(oldOffset: Int, newOffset: Int) {
        val targetIndex = findTargetOffsetIndex(oldOffset)
        slots[targetIndex] = newOffset.toShort()
        source.setShort((slots.size - targetIndex - 1) * Short.SIZE_BYTES, newOffset)
    }

    /**
     * such as [List.get]
     * param index is slot index.
     * the min slot index is 0
     * @return offset that max record in group
     */
    operator fun get(index: Int): Int {
        return slots[index].toInt()
    }

    /**
     * insert a slot into page directory.
     *
     * example:
     *  page directory: [1, 3, 5, 7]
     *  insert(1, 2)
     *  page directory: [1, 2, 3, 5, 7]
     *
     * @param index slot index. min slot is 0
     * @param offset offset that max record in group
     */
    fun insert(index: Int, offset: Int) {
        require(index >= 0 && index < slots.size) {
            "index must in 0...${slots.size - 1}"
        }
        require(offset > 0 && offset < ConstantSize.PAGE.size) {
            "offset must in 0...${ConstantSize.PAGE.size}"
        }
        slots = ShortArray(slots.size + 1) {
            if (it < index) {
                slots[it]
            } else if (it == index) {
                offset.toShort()
            } else {
                slots[it - 1]
            }
        }
        val pageSource = belongPage.source
        for (i in index..<slots.size) {
            pageSource.setShort(ConstantSize.FILE_TRAILER.offset - (i + 1) * Short.SIZE_BYTES, slots[i].toInt())
        }
        this.belongPage.pageHeader.slotCount++
    }


    fun preTargetOffset(thisOffset: Int): Int {
        return findTargetOffsetIndex(thisOffset) - 1
    }

    fun nextTargetOffset(thisOffset: Int): Int {
        return findTargetOffsetIndex(thisOffset) + 1
    }

    internal fun clear() {
        this.source.writerIndex(0)
        for (i in 0 until this.slots.size) {
            this.source.writeShort(0)
        }
        this.source.setShort(this.source.capacity() - Short.SIZE_BYTES, Infimum.OFFSET_IN_PAGE)
        this.source.setShort(this.source.capacity() - Short.SIZE_BYTES * 2, Supremum.OFFSET_IN_PAGE)
        this.slots = shortArrayOf(Infimum.OFFSET_IN_PAGE.toShort(), Supremum.OFFSET_IN_PAGE.toShort())
    }

    private fun pageBuff(): ByteBuf {
        val slotCount = belongPage.pageHeader.slotCount
        val slotLength = slotCount * Short.SIZE_BYTES
        return belongPage.source.slice(ConstantSize.FILE_TRAILER.offset - slotLength, slotLength)
    }

    private fun findTargetOffsetIndex(offset: Int): Int {
        val targetIndex = this.slots.binarySearch(offset.toShort())
        require(targetIndex != -1) {
            "target offset [$offset] is not in slot"
        }
        return targetIndex
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
        return source.hashCode()
    }

    override fun toString(): String {
        return "PageDirectory(slot size: ${slots.size})"
    }

    companion object PageDirectoryFactory {

        /**
         * non params constructor create a contains infimum and supremum offset slot.
         */
        fun create(belongPage: InnoDbPage) = PageDirectory(belongPage).apply {
            slots = shortArrayOf(ConstantSize.SUPREMUM.offset.toShort(), ConstantSize.INFIMUM.offset.toShort())
        }

        fun wrap(slots: ShortArray, belongPage: InnoDbPage) = PageDirectory(belongPage).apply {
            require(slots.size >= 2) { "slots size must be greater than 2" }
            require(slots[0] == ConstantSize.SUPREMUM.offset.toShort()) { "first slot must be supremum" }
            require(slots[slots.size - 1] == ConstantSize.INFIMUM.offset.toShort()) { "last slot must be infimum" }
            this.slots = slots
        }


    }

}
