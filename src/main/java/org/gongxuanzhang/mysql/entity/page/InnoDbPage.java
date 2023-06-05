/*
 * Copyright 2023 java-mysql  and the original author or authors <gongxuanzhangmelt@gmail.com>.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/implement-study/java-mysql/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gongxuanzhang.mysql.entity.page;


import lombok.Data;
import org.gongxuanzhang.mysql.constant.Constant;
import org.gongxuanzhang.mysql.constant.ConstantSize;
import org.gongxuanzhang.mysql.core.ByteSwappable;
import org.gongxuanzhang.mysql.core.Refreshable;
import org.gongxuanzhang.mysql.core.TableInfoBox;
import org.gongxuanzhang.mysql.entity.InsertRow;
import org.gongxuanzhang.mysql.entity.PrimaryKey;
import org.gongxuanzhang.mysql.entity.ShowLength;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.exception.RepetitionPrimaryKeyException;
import org.gongxuanzhang.mysql.service.executor.session.show.PageShower;
import org.gongxuanzhang.mysql.tool.ArrayUtils;
import org.gongxuanzhang.mysql.tool.Context;
import org.gongxuanzhang.mysql.tool.PageWriter;
import org.gongxuanzhang.mysql.tool.PrimaryKeyExtractor;

import java.nio.ByteBuffer;
import java.util.Comparator;

import static org.gongxuanzhang.mysql.tool.PageUtils.getNextUserRecord;
import static org.gongxuanzhang.mysql.tool.PageUtils.getUserRecordByOffset;

/**
 * InnoDb 页结构
 * 默认16K 暂时不支持修改
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 * @see InnoDbPageFactory
 **/

@Data
public class InnoDbPage implements ShowLength, ByteSwappable, Refreshable, Comparator<UserRecord>, TableInfoBox {


    /**
     * 文件头 38字节
     **/
    FileHeader fileHeader;
    /**
     * 页头 56字节
     **/
    PageHeader pageHeader;
    /**
     * 下确界，13字节
     **/
    Infimum infimum;
    /**
     * 上确界，13字节
     **/
    Supremum supremum;
    /**
     * 用户记录  不确定字节
     **/
    UserRecords userRecords;
    /**
     * 空闲空间，这里只记录字节数
     **/
    short freeSpace;
    /**
     * 页目录
     **/
    PageDirectory pageDirectory;
    /**
     * 文件尾 8字节
     **/
    FileTrailer fileTrailer;


    @Override
    public int length() {
        return ConstantSize.PAGE.getSize();
    }

    @Override
    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(length());
        buffer.put(fileHeader.toBytes());
        buffer.put(pageHeader.toBytes());
        buffer.put(infimum.toBytes());
        buffer.put(supremum.toBytes());
        buffer.put(userRecords.toBytes());
        buffer.position(buffer.position() + freeSpace);
        buffer.put(pageDirectory.toBytes());
        buffer.put(fileTrailer.toBytes());
        return buffer.array();
    }

    /**
     * 判断当前空闲空间是否足够
     *
     * @param length 需要的空间大小
     * @return true 是足够
     **/
    public boolean isEnough(int length) {
        return this.freeSpace >= length;
    }

    /**
     * 外部需要判断空间是否足够  否则会有问题
     * 插入目标位置
     *
     * @param row 插入数据
     **/
    public void insert(InsertRow row) throws MySQLException {
        if (!isEnough(row.length())) {
            throw new MySQLException("页选择异常");
        }
        Compact insertCompact = row.toUserRecord(Compact.class);
        int insertSlot = findInsertSlot(insertCompact);
        //  插入链表
        short preOffset = this.pageDirectory.getSlots()[insertSlot - 1];
        UserRecord preGroupMax = getUserRecordByOffset(this, preOffset);
        insertLinkedList(insertCompact, preGroupMax);
        this.freeSpace -= insertCompact.length();
        this.userRecords.add(insertCompact.toBytes());
        this.pageHeader.heapTop += insertCompact.length();
        this.pageHeader.lastInsertOffset += insertCompact.length();
        //  调整组
        UserRecord insertGroupMax = getUserRecordByOffset(this, pageDirectory.getSlots()[insertSlot]);
        int currentOwned = insertGroupMax.getRecordHeader().getNOwned() + 1;
        insertGroupMax.getRecordHeader().setnOwned(currentOwned);
        if (currentOwned > Constant.RECORD_SPLIT_SIZE) {
            groupSplit(insertSlot);
            this.freeSpace -= 2;
        }
        this.refresh();
    }


    /**
     * 组分裂
     *
     * @param slotIndex 第几个组
     **/
    private void groupSplit(int slotIndex) {
        short rightMaxOffset = this.pageDirectory.getSlots()[slotIndex];
        UserRecord rightMax = getUserRecordByOffset(this, rightMaxOffset);
        rightMax.getRecordHeader().setnOwned(5);
        UserRecord preSlotMax = getUserRecordByOffset(this, pageDirectory.getSlots()[slotIndex - 1]);
        UserRecord leftMaxPre = null;
        //  找到第四个 变成一组，偏移量需要用第三个
        for (int i = 0; i < 3; i++) {
            leftMaxPre = getNextUserRecord(this, preSlotMax);
        }
        int nextRecordOffset = leftMaxPre.getRecordHeader().getNextRecordOffset();
        short[] slots = this.pageDirectory.getSlots();
        short[] newSlots = ArrayUtils.insert(slots, slotIndex, (short) nextRecordOffset);
        this.pageDirectory.setSlots(newSlots);
        this.pageHeader.slotCount++;
    }


    /**
     * 按照长度插入链表
     *
     * @param preGroupMax 插入的当前组的上一组的最大记录。也就是说当前记录一定比这个记录大
     **/
    private void insertLinkedList(Compact insertCompact, UserRecord preGroupMax) {
        UserRecord pre = preGroupMax;
        UserRecord next = getNextUserRecord(this, pre);
        while (this.compare(insertCompact, next) > 0) {
            pre = next;
            next = getUserRecordByOffset(this, (short) pre.getRecordHeader().getNextRecordOffset());
        }
        RecordHeader insertHeader = nextRecordHeader();
        insertHeader.setNextRecordOffset(pre.getRecordHeader().getNextRecordOffset());
        pre.getRecordHeader().setNextRecordOffset(this.pageHeader.lastInsertOffset);
        insertCompact.setRecordHeader(insertHeader);
    }

    /**
     * 创建下一个插入的数据头
     **/
    private RecordHeader nextRecordHeader() {
        RecordHeader insertHeader = new RecordHeader();
        this.pageHeader.absoluteRecordCount++;
        insertHeader.setHeapNo(this.pageHeader.absoluteRecordCount);
        this.pageHeader.recordCount++;
        return insertHeader;
    }


    /**
     * 找到最终插到哪个slot中
     *
     * @return 返回需要插入的slot 必不可能是第0个槽 至少返回1
     **/
    private int findInsertSlot(Compact insertCompact) throws MySQLException {
        int left = 0;
        int right = pageDirectory.slotCount() - 1;
        while (left < right - 1) {
            int mid = (right + left) / 2;
            short offset = this.pageDirectory.getSlots()[mid];
            UserRecord base = getUserRecordByOffset(this, offset);
            int compare = this.compare(insertCompact, base);
            if (compare == 0) {
                throw new MySQLException("主键重复");
            }
            if (compare < 0) {
                right = mid;
            } else {
                left = mid;
            }
        }
        UserRecord base = getUserRecordByOffset(this, this.pageDirectory.getSlots()[left]);
        int compare = this.compare(insertCompact, base);
        if (compare == 0) {
            throw new MySQLException("主键重复");
        }
        if (compare < 0) {
            return left;
        }
        return right;
    }


    /**
     * 整理记录头
     * 调整页目录之类的
     **/
    @Override
    public void refresh() throws MySQLException {
        PageWriter.write(this);
    }

    /**
     * 是否是索引页
     **/
    public boolean isIndexPage() {
        return this.getFileHeader().getPageType() == PageType.FIL_PAGE_INODE.getValue();
    }

    /**
     * 是否是数据页
     **/
    public boolean isDataPage() {
        return this.getFileHeader().getPageType() == PageType.FIL_PAGE_INDEX.getValue();
    }

    @Override
    public int compare(UserRecord r1, UserRecord r2) {
        TableInfo tableInfo = getTableInfo();
        PrimaryKey primaryKey1 = PrimaryKeyExtractor.extract(r1, tableInfo);
        PrimaryKey primaryKey2 = PrimaryKeyExtractor.extract(r2, tableInfo);
        int compare = primaryKey1.compareTo(primaryKey2);
        if (compare == 0) {
            throw new RepetitionPrimaryKeyException(primaryKey1 + "已经存在");
        }
        return compare;
    }

    @Override
    public TableInfo getTableInfo() {
        return Context.getTableManager().select(this.fileHeader.spaceId);
    }

    @Override
    public void setTableInfo(TableInfo tableInfo) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return new PageShower(this).pageString();
    }


}
