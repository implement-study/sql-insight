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

package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page;

import lombok.Data;
import org.gongxuanzhang.easybyte.core.ByteWrapper;

import java.nio.ByteBuffer;

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
 **/
@Data
public class PageDirectory implements PageObject, ByteWrapper {


    short[] slots;

    @Override
    public int length() {
        return this.slots.length * Short.BYTES;
    }

    @Override
    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(length());
        for (short slot : slots) {
            buffer.putShort(slot);
        }
        return buffer.array();
    }

    /**
     *
     **/
    public int slotCount() {
        return slots.length;
    }


    public short indexSlot(int index) {
        return this.slots[index];
    }
}
