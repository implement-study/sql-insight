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

package org.gongxuanzhang.mysql.entity.page;


import lombok.Getter;
import org.gongxuanzhang.mysql.annotation.NotInPage;
import org.gongxuanzhang.mysql.constant.ConstantSize;
import org.gongxuanzhang.mysql.core.ByteSwappable;
import org.gongxuanzhang.mysql.entity.ShowLength;
import org.gongxuanzhang.mysql.tool.BitSetter;
import org.gongxuanzhang.mysql.tool.BitUtils;

import java.util.Arrays;

/**
 * 记录头信息 占5字节
 * 一共40位
 * ┌────────────┬────────────┬──────────────┬───────────────┬──────────┬───────────┬──────────────┬───────────────┐
 * │unuseful(1) │ unuseful(1)│delete_mask(1)│min_rec_mask(1)│n_owned(4)│heap_no(13)│record_type(3)│next_record(16)│
 * └────────────┴────────────┴──────────────┴───────────────┴──────────┴───────────┴──────────────┴───────────────┘
 * delete_mask: 删除标记
 * min_rec_mask: 非叶子节点中的最小记录，只有目录项记录此处可能是1
 * n_owned 当前组中拥有的记录数,只有组中最大的记录才会有此项记录。 页中会分成多组，为了在页中二分查找
 * heap_no 当前记录在本页中的序号，最小是记录是0 最大记录是1 用户记录是从2开始的
 * record_type 0表示普通记录 1表示非叶子结点记录 2表示最小记录 3表示最大记录
 * next_record 下一条记录的偏移量(如果物理上是连续的就是本记录的长度，如果物理上不连续就是下条记录在本页的偏移量)
 * 如果是最大记录此条为0
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
@Getter
public class RecordHeader implements ShowLength, ByteSwappable {

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

    @NotInPage("表示记录在页中的偏移量，并不在页中真实存储")
    private short pageOffset;

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
        throw new IllegalArgumentException("非法");
    }

    private void swapProperties() {
        final int deleteMask = 1 << 5;
        this.delete = (source[0] & deleteMask) == deleteMask;

        final int minRecMask = 1 << 4;
        this.minRec = (source[0] & minRecMask) == minRecMask;

        final int nOwned = 0x0F;
        this.nOwned = source[0] & nOwned;

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
            this.source[0] = BitSetter.setBitToOne(this.source[0], 5);
        } else {
            this.source[0] = BitSetter.setBitToZero(this.source[0], 5);
        }
        return this;
    }

    public RecordHeader setMinRec(boolean minRec) {
        if (this.minRec == minRec) {
            return this;
        }
        this.minRec = minRec;
        if (minRec) {
            this.source[0] = BitSetter.setBitToOne(this.source[0], 4);
        } else {
            this.source[0] = BitSetter.setBitToZero(this.source[0], 4);
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
    public int length() {
        return this.source.length;
    }


    public short getPageOffset() {
        return pageOffset;
    }

    public RecordHeader setPageOffset(short pageOffset) {
        this.pageOffset = pageOffset;
        return this;
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
}
