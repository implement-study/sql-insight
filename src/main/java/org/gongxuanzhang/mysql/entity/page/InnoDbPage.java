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
import org.gongxuanzhang.mysql.constant.ConstantSize;
import org.gongxuanzhang.mysql.core.ByteSwappable;
import org.gongxuanzhang.mysql.core.Refreshable;
import org.gongxuanzhang.mysql.core.TableInfoBox;
import org.gongxuanzhang.mysql.entity.InsertRow;
import org.gongxuanzhang.mysql.entity.ShowLength;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.tool.BitUtils;
import org.gongxuanzhang.mysql.tool.Context;
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
        //  拿到槽的上一个偏移量  插入链表
        short offset = this.pageDirectory.getSlots()[insertSlot - 1];
        insertLinkedList(insertCompact, offset);


        System.out.println(insertSlot);
        //   调整组

        TableInfo tableInfo = row.getTableInfo();
        tableInfo.getVariableCount();


//
//
//
//
//        RecordHeader nextRecordHeader = createNextRecordHeader();
//        insertData.setRecordHeader(nextRecordHeader);
//        byte[] insertBytes = insertData.toBytes();
//        this.userRecords.add(insertBytes);
//        this.freeSpace -= insertBytes.length;

    }


    /**
     * 按照长度插入链表
     **/
    private void insertLinkedList(Compact insertCompact, short offset) {
        UserRecord pre = getUserRecordByOffset(offset);
        UserRecord next = getNextUserRecord(pre);
        while (this.compare(insertCompact, next) > 0) {
            pre = next;
            next = getUserRecordByOffset((short) pre.getRecordHeader().getNextRecordOffset());
        }
        System.out.println(pre);
        System.out.println(next);
    }

    private UserRecord getNextUserRecord(UserRecord userRecord) {
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
        if (offset == PageDirectoryFactory.INFIMUM_OFFSET) {
            return this.infimum;
        }
        if (offset == PageDirectoryFactory.SUPREMUM_OFFSET) {
            return this.supremum;
        }
        int bodyOffset = offset - PageDirectoryFactory.SUPREMUM_OFFSET;
        byte[] bodySource = userRecords.getSource();
        ByteBuffer wrap = ByteBuffer.wrap(bodySource, bodyOffset, bodySource.length - bodyOffset);
        byte[] recordBuffer = ConstantSize.RECORD_HEADER.emptyBuff();
        wrap.get(recordBuffer);
        RecordHeader recordHeader = new RecordHeaderFactory().swap(recordBuffer);
        byte[] variablesBuffer = new byte[getTableInfo().getVariableCount()];
        wrap.get(variablesBuffer);
        Variables variables = new Variables(variablesBuffer);
        CompactNullValue compactNullValue = new CompactNullValue(wrap.getShort());
        int bodyLength = bodyLength(variables, compactNullValue, getTableInfo());
        byte[] body = new byte[bodyLength];
        wrap.get(body);
        Compact compact = new Compact();
        long rowId = BitUtils.readLong(wrap, 6);
        long transactionId = BitUtils.readLong(wrap, 6);
        long rollPointer = BitUtils.readLong(wrap, 7);
        compact.setBody(body);
        compact.setNullValues(compactNullValue);
        compact.setRecordHeader(recordHeader);
        compact.setRollPointer(rollPointer);
        compact.setRowId(rowId);
        compact.setTransactionId(transactionId);
        return compact;
    }


    private int bodyLength(Variables variables, CompactNullValue compactNullValue, TableInfo tableInfo) {
        //  todo
        return 0;
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
        recordHeader.setNextRecordOffset(PageDirectoryFactory.SUPREMUM_OFFSET);
        return recordHeader;
    }


    /**
     * 整理记录头
     * 调整页目录之类的
     **/
    @Override
    public void refresh() throws MySQLException {
        //  todo 刷新目录页
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
        return PrimaryKeyExtractor.extract(r1, tableInfo).compareTo(PrimaryKeyExtractor.extract(r2, tableInfo));
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
