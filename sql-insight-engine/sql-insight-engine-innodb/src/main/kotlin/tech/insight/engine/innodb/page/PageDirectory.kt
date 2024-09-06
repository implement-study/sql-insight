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
import tech.insight.buffer.getLength

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

    /**
     * page directory may change frequently.
     * So source not actually dir data. The head will carry some free space.
     *
     */
    var source: ByteBuf = pageBuff()

    /**
     * order AES in order to support binary search
     */
    internal val slots = MutableList(belongPage.pageHeader.slotCount) {
        val offsetInPage: Int = belongPage.pageHeader.slotCount * Short.SIZE_BYTES - (it + 1) * Short.SIZE_BYTES
        val recordOffset = source.getShort(offsetInPage)
        PageSlot(recordOffset.toInt(), it, this)
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
        flushSourceIfNecessary()
        slots.add(index, PageSlot(offset, index, this))
        val bytes = belongPage.source.getLength(offsetInPage(), (slots.size - index) * Short.SIZE_BYTES)
        belongPage.source.setBytes(offsetInPage() - Short.SIZE_BYTES, bytes)
        belongPage.source.setByte((ConstantSize.FILE_TRAILER.offset - index - 1) * Short.SIZE_BYTES, offset)
        this.belongPage.pageHeader.slotCount++
    }

    /**
     * @param removedIndex min slot is 0
     */
    fun removeSlot(removedIndex: Int) {
        require(removedIndex > 0 && removedIndex < slots.size - 1) {
            "removed index must in 1...${slots.size - 1} infimum and supremum can't remove"
        }
        val bytes = belongPage.source.getLength(offsetInPage(), (slots.size - removedIndex - 1) * Short.SIZE_BYTES)
        belongPage.source.setBytes(offsetInPage() + Short.SIZE_BYTES, bytes)
        belongPage.source.setByte(offsetInPage(), 0)
        this.source.setShort(0, 0)
        this.slots.removeAt(removedIndex)
        this.belongPage.pageHeader.slotCount--
    }

    fun replace(oldOffset: Int, newOffset: Int) {
        val index = slots.indexOfFirst { it.offset == oldOffset }
        require(index != -1) {
            "old offset [$oldOffset] is not in slot"
        }
        slots[index] = PageSlot(newOffset, index, this)
        source.setShort((slots.size - index - 1) * Short.SIZE_BYTES, newOffset)
    }

    /**
     * such as [List.get]
     * param index is slot index.
     * the min slot index is 0
     * @return offset that max record in group
     */
    fun getByOffset(offset: Int): PageSlot {
        return slots.first { it.offset == offset }
    }

    /**
     * Finds the PageSlot in the parent PageDirectory with the given offset.
     *
     * @param offset the offset to search for
     * @return the PageSlot with the given offset
     * @throws IllegalArgumentException if the offset is not found in the slots array
     */
    fun requireSlotByOffset(offset: Int): PageSlot {
        return slots.find { it.offset == offset } ?: throw IllegalArgumentException("offset $offset is not in slot")
    }

    /**
     * find target offset should be in slot.
     *
     * @return the slot record must be greater than target record ,
     * so never return `slot.length -1 ` because it is the infimum
     */
    fun findTargetIn(userRecord: InnodbUserRecord): PageSlot {
        if (slots.size == 2) {
            return slots[0]
        }
        val maxExcludeSupremum = belongPage.getUserRecordByOffset(slots[1].offset)
        if (this.belongPage.compare(maxExcludeSupremum, userRecord) < 0) {
            return slots[1]
        }
        var left = slots.first().bigger()
        var result = slots.last()
        var right = result.smaller()
        while (left <= right) {
            val mid = slots[left.index + ((right.index - left.index) shr 1)]
            val midRecord = mid.maxRecord()
            val compare = belongPage.compare(userRecord, midRecord)
            if (compare == 0) {
                return mid
            }
            if (compare > 0) {
                result = mid
                right = mid.smaller()
            } else {
                left = mid.bigger()
            }
        }
        return result
    }

    internal fun clear() {
        belongPage.source.setZero(offsetInPage(), length())
        this.belongPage.pageHeader.slotCount = 2
        this.slots.clear()
        slots.add(PageSlot(Infimum.OFFSET_IN_PAGE, 0, this))
        slots.add(PageSlot(Supremum.OFFSET_IN_PAGE, 1, this))
        this.source = pageBuff()
    }

    private fun flushSourceIfNecessary() {
        if (this.source.capacity() == slots.size * Short.SIZE_BYTES) {
            this.source = pageBuff()
        }
    }

    private fun pageBuff(): ByteBuf {
        val slotCount = belongPage.pageHeader.slotCount
        val slotLength = slotCount * Short.SIZE_BYTES + INIT_FREE_SPACE
        return belongPage.source.slice(ConstantSize.FILE_TRAILER.offset - slotLength, slotLength)
    }

    override fun length(): Int {
        return slots.size * Short.SIZE_BYTES
    }

    override fun toBytes(): ByteArray {
        return source.getLength(offsetInPage(), length())
    }


    private fun offsetInPage(): Int {
        return ConstantSize.FILE_TRAILER.offset - belongPage.pageHeader.slotCount * Short.SIZE_BYTES
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PageDirectory) return false
        return true
    }

    override fun hashCode(): Int {
        return source.hashCode()
    }

    override fun toString(): String {
        return "PageDirectory(slot size: ${slots.size})"
    }

    data class PageSlot(val offset: Int, val index: Int, val parent: PageDirectory) {

        /**
         * Returns the previous slot in the parent PageDirectory.
         *
         * @return the previous PageSlot
         */
        fun smaller(): PageSlot {
            require(index > 0) {
                "this slot is infimum, can't find smaller"
            }
            return parent.slots[index - 1]
        }

        /**
         * Returns the next slot in the parent PageDirectory.
         *
         * @return the next PageSlot
         */
        fun bigger(): PageSlot {
            require(index < parent.slots.size - 1) {
                "this slot is supremum, can't find bigger"
            }
            return parent.slots[index + 1]
        }

        fun maxRecord(): InnodbUserRecord {
            return this.parent.belongPage.getUserRecordByOffset(offset)
        }

        fun minRecord(): InnodbUserRecord {
            if (this.index == 0) {
                return parent.belongPage.infimum
            }
            return this.smaller().maxRecord()
        }

        operator fun compareTo(other: PageSlot): Int {
            require(this.parent === other.parent) {
                "The two slots to be compared must belong to the same PageDirectory"
            }
            return this.index.compareTo(other.index)
        }

        fun remove() {
            this.parent.removeSlot(this.index)
        }
    }

    companion object {
        const val INIT_FREE_SPACE = 32 * Short.SIZE_BYTES
    }
}
