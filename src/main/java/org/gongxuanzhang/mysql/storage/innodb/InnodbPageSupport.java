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

package org.gongxuanzhang.mysql.storage.innodb;

import org.gongxuanzhang.mysql.constant.Constant;
import org.gongxuanzhang.mysql.constant.ConstantSize;
import org.gongxuanzhang.mysql.core.TableInfoBox;
import org.gongxuanzhang.mysql.entity.Column;
import org.gongxuanzhang.mysql.entity.InsertRow;
import org.gongxuanzhang.mysql.entity.PrimaryKey;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.entity.page.*;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.exception.RepetitionPrimaryKeyException;
import org.gongxuanzhang.mysql.tool.ArrayUtils;
import org.gongxuanzhang.mysql.tool.BitUtils;
import org.gongxuanzhang.mysql.tool.Context;
import org.gongxuanzhang.mysql.tool.PrimaryKeyExtractor;

import java.nio.ByteBuffer;
import java.util.Comparator;

/**
 * 各种关于 innodb page的操作
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class InnodbPageSupport implements Comparator<UserRecord>, TableInfoBox {

    private final InnoDbPage page;

    public InnodbPageSupport(InnoDbPage page) {
        this.page = page;
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
        short preOffset = this.page.getPageDirectory().getSlots()[insertSlot - 1];
        UserRecord preGroupMax = getUserRecordByOffset(preOffset);
        insertLinkedList(insertCompact, preGroupMax);
        //  调整组
        UserRecord insertGroupMax = getUserRecordByOffset(page.getPageDirectory().getSlots()[insertSlot]);
        int currentOwned = insertGroupMax.getRecordHeader().getNOwned() + 1;
        insertGroupMax.getRecordHeader().setnOwned(currentOwned);
        if (currentOwned > Constant.RECORD_SPLIT_SIZE) {
            groupSplit(insertSlot);
            this.freeSpace -= 2;
        }
        this.refresh();
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
     * 组分裂
     *
     * @param slotIndex 第几个组
     **/
    private void groupSplit(int slotIndex) {
        short rightMaxOffset = this.page.getPageDirectory().getSlots()[slotIndex];
        UserRecord rightMax = getUserRecordByOffset(rightMaxOffset);
        rightMax.getRecordHeader().setnOwned(5);
        UserRecord preSlotMax = getUserRecordByOffset(this.page.getPageDirectory().getSlots()[slotIndex - 1]);
        UserRecord leftMaxPre = null;
        //  找到第四个 变成一组，偏移量需要用第三个
        for (int i = 0; i < 3; i++) {
            leftMaxPre = getNextUserRecord(preSlotMax);
        }
        int nextRecordOffset = leftMaxPre.getRecordHeader().getNextRecordOffset();
        short[] slots = this.page.getPageDirectory().getSlots();
        short[] newSlots = ArrayUtils.insert(slots, slotIndex, (short) nextRecordOffset);
        this.page.getPageDirectory().setSlots(newSlots);
        this.page.getPageHeader().setSlotCount((short) (this.page.getPageHeader().getSlotCount() + 1));
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
        pre.getRecordHeader().setNextRecordOffset(this.page.getPageHeader().getLastInsertOffset());
        insertCompact.setRecordHeader(insertHeader);
        page.setFreeSpace((short) (page.getFreeSpace() - insertCompact.length()));
        page.getUserRecords().add(insertCompact.toBytes());

        PageHeader pageHeader = this.page.getPageHeader();
        pageHeader.setHeapTop((short) (pageHeader.getHeapTop() + insertCompact.length()));
        pageHeader.setLastInsertOffset((short) (pageHeader.getLastInsertOffset() + insertCompact.length()));
    }

    /**
     * 创建下一个插入的数据头
     **/
    private RecordHeader nextRecordHeader() {
        RecordHeader insertHeader = new RecordHeader();
        PageHeader pageHeader = this.page.getPageHeader();
        pageHeader.setAbsoluteRecordCount((short) (pageHeader.getAbsoluteRecordCount() + 1));
        insertHeader.setHeapNo(pageHeader.getAbsoluteRecordCount());
        pageHeader.setRecordCount((short) (pageHeader.getRecordCount() + 1));
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


}
