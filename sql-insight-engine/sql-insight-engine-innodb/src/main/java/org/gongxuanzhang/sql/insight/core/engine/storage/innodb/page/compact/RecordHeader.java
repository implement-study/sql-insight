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

package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact;


import lombok.Getter;
import org.gongxuanzhang.easybyte.core.ByteWrapper;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.ConstantSize;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.PageObject;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.utils.BitOperator;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.utils.BitUtils;

import java.util.Arrays;

/**
 * 5 bytes 40 bits.
 * ┌────────────┬────────────┬──────────────┬───────────────┬──────────┬───────────┬──────────────┬───────────────┐
 * │unuseful(1) │ unuseful(1)│delete_mask(1)│min_rec_mask(1)│n_owned(4)│heap_no(13)│record_type(3)│next_record(16)│
 * └────────────┴────────────┴──────────────┴───────────────┴──────────┴───────────┴──────────────┴───────────────┘
 * delete_mask
 * min_rec_mask: whether non-leaf node min record，only index node may be 1
 * n_owned : records count in group. only max record in group have this count.
 * heap_no: the record number in page, infimum is 0, supremum is 1,user record start with 2.
 * record_type:0 normal record 1 non leaf node (index) 2 infimum 3 supremum
 * next_record:next record offset in this page. supremum next_record is 0
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
@Getter
public class RecordHeader implements ByteWrapper, PageObject {

    /**
     * 记录头本质上只有5个字节 但是40bit有不同作用
     **/
    private final byte[] source;
    private boolean delete;
    private boolean minRec;
    private int nOwned;
    private int heapNo;
    private int nextRecordOffset;
    private RecordType recordType;


    public RecordHeader() {
        this.source = new byte[5];
    }

    public RecordHeader(byte[] source) {
        ConstantSize.RECORD_HEADER.checkSize(source);
        this.source = source;
        swapProperties();
    }

    private void initType() {
        int type = source[2] & 0x07;
        for (RecordType recordType : RecordType.values()) {
            if (recordType.value == type) {
                this.recordType = recordType;
                return;
            }
        }
        throw new IllegalArgumentException();
    }

    private void swapProperties() {
        final int deleteMask = 1 << 5;
        this.delete = (source[0] & deleteMask) == deleteMask;

        final int minRecMask = 1 << 4;
        this.minRec = (source[0] & minRecMask) == minRecMask;

        final int nOwnedBase = 0x0F;
        this.nOwned = source[0] & nOwnedBase;

        int high = Byte.toUnsignedInt(source[1]);
        int low = Byte.toUnsignedInt(source[2]);
        this.heapNo = ((high << 8 | low) >> 3);

        high = Byte.toUnsignedInt(source[3]);
        low = Byte.toUnsignedInt(source[4]);
        this.nextRecordOffset = (high << 8 | low);

        initType();
    }

    public RecordHeader setDelete(boolean delete) {
        if (this.delete == delete) {
            return this;
        }
        this.delete = delete;
        if (delete) {
            this.source[0] = BitOperator.setBitToOne(this.source[0], 5);
        } else {
            this.source[0] = BitOperator.setBitToZero(this.source[0], 5);
        }
        return this;
    }

    public RecordHeader setMinRec(boolean minRec) {
        if (this.minRec == minRec) {
            return this;
        }
        this.minRec = minRec;
        if (minRec) {
            this.source[0] = BitOperator.setBitToOne(this.source[0], 4);
        } else {
            this.source[0] = BitOperator.setBitToZero(this.source[0], 4);
        }
        return this;
    }

    public RecordHeader setnOwned(int nOwned) {
        if (this.nOwned == nOwned) {
            return this;
        }
        //  清零source[0]的后四位
        this.source[0] &= 0xF0;
        this.source[0] |= nOwned;
        this.nOwned = nOwned;
        return this;
    }

    public RecordHeader setHeapNo(int heapNo) {
        if (this.heapNo == heapNo) {
            return this;
        }
        this.heapNo = heapNo;
        this.source[1] = (byte) (heapNo >> 5);
        this.source[2] &= 0b00000111;
        this.source[2] |= (byte) (heapNo << 3);
        return this;
    }


    public RecordHeader setNextRecordOffset(int nextRecordOffset) {
        if (this.nextRecordOffset == nextRecordOffset) {
            return this;
        }
        this.nextRecordOffset = nextRecordOffset;
        byte[] bytes = BitUtils.cutToByteArray(nextRecordOffset, 2);
        this.source[3] = bytes[0];
        this.source[4] = bytes[1];
        return this;
    }

    public RecordHeader setRecordType(RecordType recordType) {
        if (this.recordType == recordType) {
            return this;
        }
        this.recordType = recordType;
        // 后三位置0
        this.source[2] &= 0b11111000;
        this.source[2] |= recordType.value;
        return this;
    }

    @Override
    public byte[] toBytes() {
        return this.source;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RecordHeader that = (RecordHeader) o;
        return Arrays.equals(source, that.source);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(source);
    }

    @Override
    public int length() {
        return source.length;
    }
}