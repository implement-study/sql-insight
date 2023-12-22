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

package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page;


import lombok.Data;
import org.gongxuanzhang.easybyte.core.ByteWrapper;
import org.gongxuanzhang.easybyte.core.DynamicByteBuffer;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.Compact;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.RowFormatFactory;
import org.gongxuanzhang.sql.insight.core.exception.DuplicationPrimaryKeyException;
import org.gongxuanzhang.sql.insight.core.object.Table;
import org.gongxuanzhang.sql.insight.core.object.UserRecord;

/**
 * InnoDb Page
 * size default 16K.
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/

@Data
public abstract class InnoDbPage implements ByteWrapper {


    /**
     * 38 bytes
     **/
    FileHeader fileHeader;
    /**
     * 56 bytes
     **/
    PageHeader pageHeader;
    /**
     * 13 bytes
     **/
    Infimum infimum;
    /**
     * 13 bytes
     **/
    Supremum supremum;
    /**
     * uncertain bytes.
     * user records bytes + freeSpace = page size - other fixed size
     **/
    UserRecords userRecords;
    /**
     * uncertain bytes.
     **/
    short freeSpace;
    /**
     * uncertain bytes.
     **/
    PageDirectory pageDirectory;
    /**
     * 8 bytes
     **/
    FileTrailer fileTrailer;

    byte[] source;

    Table table;

    protected InnoDbPage(Table table) {
        this.table = table;
    }

    @Override
    public byte[] toBytes() {
        if (source != null) {
            return source;
        }
        DynamicByteBuffer buffer = DynamicByteBuffer.allocate();
        buffer.append(fileHeader.toBytes());
        buffer.append(pageHeader.toBytes());
        buffer.append(infimum.toBytes());
        buffer.append(supremum.toBytes());
        buffer.append(userRecords.toBytes());
        buffer.append(new byte[freeSpace]);
        buffer.append(pageDirectory.toBytes());
        buffer.append(fileTrailer.toBytes());
        this.source = buffer.toBytes();
        return this.source;
    }


    public PageType pageType() {
        return PageType.valueOf(this.fileHeader.pageType);
    }


    /**
     * add a data.
     * data means a insert row .
     * page is leaf node will insert data.
     * page is index node will find target leaf node and insert data.
     * may be split page in process
     **/
    public abstract void insertData(Compact data);


    /**
     * find insert slot index in this page.
     *
     * @return result must be greater than 0 because 0 only contains infimum, but the slot may be already full
     **/
    protected int findTargetSlot(Compact insertCompact) {
        int left = 0;
        int right = pageDirectory.slotCount() - 1;
        while (left < right - 1) {
            int mid = (right + left) / 2;
            short offset = this.pageDirectory.getSlots()[mid];
            UserRecord base = getUserRecordByOffset(offset);
            int compare = Long.compare(insertCompact.getRowId(), base.getRowId());
            if (compare == 0) {
                throw new DuplicationPrimaryKeyException(base.getRowId());
            }
            if (compare < 0) {
                right = mid;
            } else {
                left = mid;
            }
        }
        UserRecord base = getUserRecordByOffset(this.pageDirectory.getSlots()[left]);
        int compare = Long.compare(insertCompact.getRowId(), base.getRowId());
        if (compare == 0) {
            throw new DuplicationPrimaryKeyException(base.getRowId());
        }
        if (compare < 0) {
            return left;
        }
        return right;
    }

    /**
     * @param offset offset in page
     * @return user record
     **/
    protected InnodbUserRecord getUserRecordByOffset(short offset) {
        if (offset == ConstantSize.INFIMUM.offset()) {
            return this.infimum;
        }
        if (offset == ConstantSize.SUPREMUM.offset()) {
            return this.supremum;
        }
        return RowFormatFactory.reaRecordInPage(this, offset, table);
    }


    /**
     * free space if enough
     * default implementation is always retain one-sixteenth page size
     *
     * @param insertLength insert length
     **/
    public boolean isEnough(int insertLength) {
        return this.freeSpace - insertLength >= ConstantSize.PAGE.size() >> 4;
    }
//
//
//
//    /**
//     * 组分裂
//     *
//     * @param slotIndex 第几个组
//     **/
//    private void groupSplit(int slotIndex) {
//        short rightMaxOffset = this.pageDirectory.getSlots()[slotIndex];
//        UserRecord rightMax = getUserRecordByOffset(this, rightMaxOffset);
//        rightMax.getRecordHeader().setnOwned(5);
//        UserRecord preSlotMax = getUserRecordByOffset(this, pageDirectory.getSlots()[slotIndex - 1]);
//        UserRecord leftMaxPre = null;
//        //  找到第四个 变成一组，偏移量需要用第三个
//        for (int i = 0; i < 3; i++) {
//            leftMaxPre = getNextUserRecord(this, preSlotMax);
//        }
//        int nextRecordOffset = leftMaxPre.getRecordHeader().getNextRecordOffset();
//        short[] slots = this.pageDirectory.getSlots();
//        short[] newSlots = ArrayUtils.insert(slots, slotIndex, (short) nextRecordOffset);
//        this.pageDirectory.setSlots(newSlots);
//        this.pageHeader.slotCount++;
//    }
//
//
//    /**
//     * 按照长度插入链表
//     *
//     * @param preGroupMax 插入的当前组的上一组的最大记录。也就是说当前记录一定比这个记录大
//     **/
//    private void insertLinkedList(Compact insertCompact, UserRecord preGroupMax) {
//        UserRecord pre = preGroupMax;
//        UserRecord next = getNextUserRecord(this, pre);
//        while (this.compare(insertCompact, next) > 0) {
//            pre = next;
//            next = getNextUserRecord(this, pre);
//        }
//        RecordHeader insertHeader = nextRecordHeader();
//        insertHeader.setNextRecordOffset(pre.getRecordHeader().getNextRecordOffset());
//        pre.getRecordHeader().setNextRecordOffset(this.pageHeader.lastInsertOffset);
//        short pageOffset = (short) pre.pageOffset();
//        //  把上一个偏移量写回页
//        if (pageOffset >= ConstantSize.USER_RECORDS.offset()) {
//            int bodyOffset = pageOffset - ConstantSize.USER_RECORDS.offset();
//            byte[] nextOffsetBytes = BitUtils.cutToByteArray(this.pageHeader.lastInsertOffset, 2);
//            this.userRecords.source[bodyOffset + 3] = nextOffsetBytes[0];
//            this.userRecords.source[bodyOffset + 4] = nextOffsetBytes[1];
//        }
//        insertCompact.setRecordHeader(insertHeader);
//        this.freeSpace -= insertCompact.length();
//        this.userRecords.add(insertCompact.toBytes());
//        this.pageHeader.heapTop += insertCompact.length();
//        this.pageHeader.lastInsertOffset += insertCompact.length();
//    }
//
//    /**
//     * 创建下一个插入的数据头
//     **/
//    private RecordHeader nextRecordHeader() {
//        RecordHeader insertHeader = new RecordHeader();
//        this.pageHeader.absoluteRecordCount++;
//        insertHeader.setHeapNo(this.pageHeader.absoluteRecordCount);
//        this.pageHeader.recordCount++;
//        return insertHeader;
//    }
//
//

//
//
//    /**
//     * 整理记录头
//     * 调整页目录之类的
//     **/
//    @Override
//    public void refresh() throws MySQLException {
//        PageWriter.write(this);
//    }
//
//
//    @Override
//    public int compare(UserRecord r1, UserRecord r2) {
//        TableInfo tableInfo = getTableInfo();
//        PrimaryKey primaryKey1 = r1.getPrimaryKey(tableInfo);
//        PrimaryKey primaryKey2 = r2.getPrimaryKey(tableInfo);
//        int compare = primaryKey1.compareTo(primaryKey2);
//        if (compare == 0) {
//            throw new RepetitionPrimaryKeyException(primaryKey1 + "已经存在");
//        }
//        return compare;
//    }
//
//    @Override
//    public TableInfo getTableInfo() {
//        return Context.getTableManager().select(this.fileHeader.spaceId);
//    }
//
//    @Override
//    public void setTableInfo(TableInfo tableInfo) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public String toString() {
//        return new PageShower(this).pageString();
//    }


}
