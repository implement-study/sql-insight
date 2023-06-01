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
import org.gongxuanzhang.mysql.entity.Column;
import org.gongxuanzhang.mysql.entity.InsertRow;
import org.gongxuanzhang.mysql.entity.PrimaryKey;
import org.gongxuanzhang.mysql.entity.ShowLength;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.exception.RepetitionPrimaryKeyException;
import org.gongxuanzhang.mysql.service.executor.session.show.PageShower;
import org.gongxuanzhang.mysql.tool.ArrayUtils;
import org.gongxuanzhang.mysql.tool.BitUtils;
import org.gongxuanzhang.mysql.tool.Context;
import org.gongxuanzhang.mysql.tool.PageWriter;
import org.gongxuanzhang.mysql.tool.PrimaryKeyExtractor;

import java.nio.ByteBuffer;
import java.util.Comparator;

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
        UserRecord preGroupMax = getUserRecordByOffset(preOffset);
        insertLinkedList(insertCompact, preGroupMax);
        this.freeSpace -= insertCompact.length();
        this.userRecords.add(insertCompact.toBytes());
        this.pageHeader.heapTop += insertCompact.length();
        this.pageHeader.lastInsertOffset += insertCompact.length();
        //  调整组
        UserRecord insertGroupMax = getUserRecordByOffset(this.pageDirectory.getSlots()[insertSlot]);
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
        UserRecord rightMax = getUserRecordByOffset(rightMaxOffset);
        rightMax.getRecordHeader().setnOwned(5);
        UserRecord preSlotMax = getUserRecordByOffset(this.pageDirectory.getSlots()[slotIndex - 1]);
        UserRecord leftMaxPre = null;
        //  找到第四个 变成一组，偏移量需要用第三个
        for (int i = 0; i < 3; i++) {
            leftMaxPre = getNextUserRecord(preSlotMax);
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
        UserRecord next = getNextUserRecord(pre);
        while (this.compare(insertCompact, next) > 0) {
            pre = next;
            next = getUserRecordByOffset((short) pre.getRecordHeader().getNextRecordOffset());
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
     * 拿到目标记录的下一个记录
     *
     * @param userRecord 目标记录
     * @return 返回记录的下一个
     **/
    private UserRecord getNextUserRecord(UserRecord userRecord) {
        if (userRecord instanceof Supremum) {
            throw new NullPointerException("supremum 没有下一个");
        }
        int nextRecordOffset = userRecord.getRecordHeader().getNextRecordOffset();
        return getUserRecordByOffset((short) nextRecordOffset);
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
            UserRecord base = getUserRecordByOffset(offset);
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
        UserRecord base = getUserRecordByOffset(this.pageDirectory.getSlots()[left]);
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
     * 通过偏移量直接拿到目标位置的用户记录
     *
     * @param offset slot里保存的偏移量
     **/
    private UserRecord getUserRecordByOffset(short offset) {
        if (offset == ConstantSize.INFIMUM.offset()) {
            return this.infimum;
        }
        if (offset == ConstantSize.SUPREMUM.offset()) {
            return this.supremum;
        }
        int bodyOffset = offset - ConstantSize.SUPREMUM.offset() - ConstantSize.SUPREMUM.getSize();
        byte[] bodySource = userRecords.getSource();
        ByteBuffer wrap = ByteBuffer.wrap(bodySource);
        wrap.position(bodyOffset);
        byte[] recordBuffer = ConstantSize.RECORD_HEADER.emptyBuff();
        wrap.get(recordBuffer);
        RecordHeader recordHeader = new RecordHeaderFactory().swap(recordBuffer);
        byte[] variablesBuffer = new byte[getTableInfo().getVariableCount()];
        wrap.get(variablesBuffer);
        Variables variables = new Variables(variablesBuffer);
        CompactNullValue compactNullValue = new CompactNullValue(wrap.getShort());
        Compact compact = new Compact();
        long rowId = BitUtils.readLong(wrap, 6);
        long transactionId = BitUtils.readLong(wrap, 6);
        long rollPointer = BitUtils.readLong(wrap, 7);
        int bodyLength = bodyLength(variables, compactNullValue, getTableInfo());
        byte[] body = new byte[bodyLength];
        wrap.get(body);
        compact.setBody(body);
        compact.setVariables(variables);
        compact.setNullValues(compactNullValue);
        compact.setRecordHeader(recordHeader);
        compact.setRollPointer(rollPointer);
        compact.setRowId(rowId);
        compact.setTransactionId(transactionId);
        return compact;
    }


    /**
     * 拿到body真正的长度
     * 一个列只有三种情况，是null，可变，固定
     * 所以把null和固定的加起来，然后把可边长总长度加起来就oK
     *
     * @param variables        可变长度
     * @param compactNullValue null值列表
     * @param tableInfo        表结构
     * @return body 长度
     **/
    private int bodyLength(Variables variables, CompactNullValue compactNullValue, TableInfo tableInfo) {
        int bodyLength = 0;
        for (int i = 0; i < tableInfo.getColumns().size(); i++) {
            Column currentCol = tableInfo.getColumns().get(i);
            Integer nullIndex = currentCol.getNullIndex();
            if (nullIndex != null && compactNullValue.isNull(nullIndex)) {
                continue;
            }
            if (!currentCol.isDynamic()) {
                bodyLength += currentCol.getLength();
            }
        }
        bodyLength += variables.variableLength();
        return bodyLength;
    }


    /**
     * 创建下一个记录头
     * 这个记录头不会因为组内数量多而进行组分裂，
     * 组分裂是由refresh操作的
     *
     * @return 下一个记录头
     **/
    private RecordHeader createNextRecordHeader() {
        short recordCount = this.pageHeader.recordCount;
        RecordHeader recordHeader = new RecordHeader();
        recordHeader.setHeapNo(recordCount + 1);
        //  暂时指向supremum
        recordHeader.setNextRecordOffset(ConstantSize.SUPREMUM.offset());
        return recordHeader;
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
