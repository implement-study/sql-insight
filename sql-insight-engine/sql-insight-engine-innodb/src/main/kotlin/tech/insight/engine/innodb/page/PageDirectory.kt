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

import org.gongxuanzhang.easybyte.core.ByteWrapper
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
class PageDirectory : PageObject, ByteWrapper {

    var slots: ShortArray

    /**
     * non params constructor create a contains infimum and supremum offset slot.
     */
    constructor() {
        slots = shortArrayOf(ConstantSize.SUPREMUM.offset().toShort(), ConstantSize.INFIMUM.offset().toShort())
    }

    constructor(slots: ShortArray) {
        this.slots = slots
    }

    fun split(splitSlotIndex: Int, newGroupMaxOffset: Short) {
        val newSlots = ShortArray(slots.size + 1)
        for (i in 0..splitSlotIndex) {
            newSlots[i] = slots[i]
        }
        newSlots[splitSlotIndex + 1] = newGroupMaxOffset
        for (i in splitSlotIndex + 1 until newSlots.size) {
            newSlots[i] = slots[i - 1]
        }
        slots = newSlots
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
}
