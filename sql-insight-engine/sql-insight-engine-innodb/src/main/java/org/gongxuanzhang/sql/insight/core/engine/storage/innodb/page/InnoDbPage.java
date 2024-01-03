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


import kotlin.Pair;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.easybyte.core.ByteWrapper;
import org.gongxuanzhang.easybyte.core.DynamicByteBuffer;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.factory.PageFactory;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.factory.PageHeaderFactory;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.index.InnodbIndex;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.IndexRecord;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.RecordHeader;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.RecordType;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.utils.PageSupport;
import org.gongxuanzhang.sql.insight.core.exception.DuplicationPrimaryKeyException;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.Constant.SLOT_MAX_COUNT;
import static org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.ConstantSize.FILE_HEADER;
import static org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.ConstantSize.FILE_TRAILER;
import static org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.ConstantSize.INFIMUM;
import static org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.ConstantSize.PAGE;
import static org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.ConstantSize.PAGE_HEADER;
import static org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.ConstantSize.SUPREMUM;

/**
 * InnoDb Page
 * size default 16K.
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/

@Data
@Slf4j
public abstract class InnoDbPage implements ByteWrapper, Comparator<InnodbUserRecord>, PageObject,
        Iterable<InnodbUserRecord> {


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
    PageDirectory pageDirectory;
    /**
     * 8 bytes
     **/
    FileTrailer fileTrailer;

    PageExt ext;

    protected InnoDbPage(InnodbIndex index) {
        this.ext = new PageExt();
        this.ext.belongIndex = index;
    }

    @Override
    public byte[] toBytes() {
        DynamicByteBuffer buffer = DynamicByteBuffer.allocate();
        buffer.append(fileHeader.toBytes());
        buffer.append(pageHeader.toBytes());
        buffer.append(infimum.toBytes());
        buffer.append(supremum.toBytes());
        buffer.append(userRecords.toBytes());
        buffer.append(new byte[getFreeSpace()]);
        buffer.append(pageDirectory.toBytes());
        buffer.append(fileTrailer.toBytes());
        return buffer.toBytes();
    }


    public PageType pageType() {
        return PageType.valueOf(this.fileHeader.pageType);
    }

    public short getFreeSpace() {
        return (short) (PAGE.size() -
                PAGE_HEADER.size() -
                FILE_HEADER.size() -
                FILE_TRAILER.size() -
                SUPREMUM.size() -
                INFIMUM.size() -
                this.pageDirectory.length() - userRecords.length());
    }

    /**
     * add a data.
     * data means a insert row .
     * page is leaf node will insert data.
     * page is index node will find target leaf node and insert data.
     * may be split page in process
     **/
    public void insertData(InnodbUserRecord data) {
        Pair<InnodbUserRecord, InnodbUserRecord> preAndNext = findPreAndNext(data);
        InnodbUserRecord pre = preAndNext.getFirst();
        InnodbUserRecord next = preAndNext.getSecond();
        linkedAndAdjust(pre, data, next);
        splitIfNecessary();
        PageSupport.flushPage(this);
    }


    /**
     * find location where the record linked in page and return pre and next.
     * search only,don't affect the page .
     *
     * @param userRecord target record
     **/
    protected Pair<InnodbUserRecord, InnodbUserRecord> findPreAndNext(InnodbUserRecord userRecord) {
        int targetSlot = findTargetSlot(userRecord);
        InnodbUserRecord pre = getUserRecordByOffset(pageDirectory.indexSlot(targetSlot + 1));
        InnodbUserRecord next = getUserRecordByOffset(pre.nextRecordOffset() + pre.offset());
        while (this.compare(userRecord, next) > 0) {
            pre = next;
            next = getUserRecordByOffset(pre.nextRecordOffset() + pre.offset());
        }
        return new Pair<>(pre, next);
    }


    /**
     * find the slot where the target record is located
     * <p>
     * return 0 means supremum
     *
     * @return slot index is user record inserted never return {@code slot.length -1 } because slot.length - 1 is the
     * infimum
     **/
    protected int findTargetSlot(InnodbUserRecord userRecord) {
        int left = 0;
        int right = pageDirectory.slotCount() - 1;
        while (left < right - 1) {
            int mid = (right + left) / 2;
            short offset = this.pageDirectory.getSlots()[mid];
            InnodbUserRecord base = getUserRecordByOffset(offset);
            int compare = this.compare(userRecord, base);
            if (compare == 0) {
                throw new DuplicationPrimaryKeyException(base.getRowId());
            }
            if (compare < 0) {
                left = mid;
            } else {
                right = mid;
            }
        }
        InnodbUserRecord base = getUserRecordByOffset(this.pageDirectory.getSlots()[right]);
        int compare = this.compare(userRecord, base);
        if (compare == 0) {
            throw new DuplicationPrimaryKeyException(base.getRowId());
        }
        if (compare > 0) {
            return left;
        }
        return right;
    }

    /**
     * @param offsetInPage offset in page
     * @return user record
     **/
    protected InnodbUserRecord getUserRecordByOffset(int offsetInPage) {
        if (offsetInPage == ConstantSize.INFIMUM.offset()) {
            return this.infimum;
        }
        if (offsetInPage == ConstantSize.SUPREMUM.offset()) {
            return this.supremum;
        }
        InnodbUserRecord wrap = wrapUserRecord(offsetInPage);
        wrap.setOffset(offsetInPage);
        return wrap;
    }


    protected abstract InnodbUserRecord wrapUserRecord(int offsetInPage);


    /**
     * this page should split.
     * in general after insert row call this method
     **/
    protected abstract void splitIfNecessary();


    protected void upgrade(InnoDbPage prePage, InnoDbPage nextPage) {
        //  is root page
        if (this.ext.parent == null) {
            rootPageUpgrade(prePage, nextPage);
        } else {
            // normal leaf node
            prePage.fileHeader.setOffset(this.fileHeader.offset);
            int newDataPageOffset = PageSupport.allocatePage(this.ext.belongIndex);
            nextPage.fileHeader.setOffset(newDataPageOffset);
            InnoDbPage parent = this.ext.parent;
            this.transferFrom(prePage);
            parent.insertData(nextPage.pageIndex());
        }
    }


    /**
     * get the first index node to parent node insert
     **/
    public abstract IndexRecord pageIndex();


    /**
     * root page upgrade.
     * create 2 new page and linked whether upgrading  from index page or data page.
     **/
    private void rootPageUpgrade(InnoDbPage preChild, InnoDbPage secondChild) {
        preChild.pageHeader.level = this.pageHeader.level;
        secondChild.pageHeader.level = this.pageHeader.level;
        this.pageHeader.level++;
        FileHeader firstFileHeader = preChild.getFileHeader();
        FileHeader secondFileHeader = secondChild.getFileHeader();
        int offset = PageSupport.allocatePage(this.ext.belongIndex, 2);
        firstFileHeader.setOffset(offset);
        secondFileHeader.setOffset(offset + ConstantSize.PAGE.size());
        firstFileHeader.setPre(-1);
        firstFileHeader.setNext(secondFileHeader.offset);
        secondFileHeader.setPre(firstFileHeader.offset);
        secondFileHeader.setNext(-1);
        //  transfer to index page
        this.fileHeader.next = -1;
        this.fileHeader.pageType = PageType.FIL_PAGE_INODE.getValue();
        this.pageHeader = PageHeaderFactory.createPageHeader();
        this.pageDirectory = new PageDirectory();
        //  clear user record
        this.userRecords = new UserRecords();
        this.insertData(preChild.pageIndex());
        this.insertData(secondChild.pageIndex());
    }


    protected void linkedAndAdjust(InnodbUserRecord pre, InnodbUserRecord insertRecord, InnodbUserRecord next) {
        RecordHeader insertHeader = insertRecord.getRecordHeader();
        insertHeader.setHeapNo(this.pageHeader.absoluteRecordCount);
        insertRecord.setOffset(this.pageHeader.lastInsertOffset + insertRecord.beforeSplitOffset());
        insertHeader.setNextRecordOffset(next.offset() - insertRecord.offset());
        pre.getRecordHeader().setNextRecordOffset(insertRecord.offset() - pre.offset());
        refreshRecordHeader(pre);

        insertHeader.setRecordType(RecordType.NORMAL);

        //  adjust page
        this.userRecords.addRecord(insertRecord);
        this.pageHeader.absoluteRecordCount++;
        this.pageHeader.recordCount++;
        this.pageHeader.heapTop += (short) insertRecord.length();
        this.pageHeader.lastInsertOffset += (short) insertRecord.length();


        //  adjust group
        while (next.getRecordHeader().getNOwned() == 0) {
            next = getUserRecordByOffset(next.offset() + next.nextRecordOffset());
        }
        RecordHeader nextHeader = next.getRecordHeader();
        int groupCount = nextHeader.getNOwned();
        nextHeader.setNOwned(groupCount + 1);
        if (next.getRecordHeader().getNOwned() <= SLOT_MAX_COUNT) {
            return;
        }
        log.info("start group split ...");
        for (int i = 0; i < this.pageDirectory.slots.length - 1; i++) {
            if (this.pageDirectory.slots[i] == next.offset()) {
                InnodbUserRecord preGroupMax = getUserRecordByOffset(this.pageDirectory.slots[i + 1]);
                for (int j = 0; j < SLOT_MAX_COUNT >> 1; j++) {
                    preGroupMax = getUserRecordByOffset(preGroupMax.offset() + preGroupMax.nextRecordOffset());
                }
                this.pageDirectory.split(i, (short) preGroupMax.offset());
            }
        }
        log.info("end group split ...");
    }


    /**
     * byte array copy from target page
     **/
    public void transferFrom(InnoDbPage page) {
        InnoDbPage snapshot = PageFactory.swap(page.toBytes(), this.ext.belongIndex);
        this.fileHeader = snapshot.fileHeader;
        this.pageHeader = snapshot.pageHeader;
        this.infimum = snapshot.infimum;
        this.supremum = snapshot.supremum;
        this.userRecords = snapshot.userRecords;
        this.pageDirectory = snapshot.pageDirectory;
        this.fileTrailer = snapshot.fileTrailer;
        this.ext = snapshot.ext;
    }

    private void refreshRecordHeader(InnodbUserRecord record) {
        if(record instanceof Infimum){
            return;
        }
        if(record instanceof Supremum){
            return;
        }
        byte[] headerByte = record.getRecordHeader().toBytes();
        int bodyOffset = record.offset() - ConstantSize.RECORD_HEADER.size() - PAGE_HEADER.size() -
                FILE_HEADER.size() -
                SUPREMUM.size() -
                INFIMUM.size();
        System.arraycopy(headerByte, 0, this.userRecords.body, bodyOffset, headerByte.length);
    }

    @Override
    public int length() {
        return ConstantSize.PAGE.size();
    }

    @NotNull
    @Override
    public Iterator<InnodbUserRecord> iterator() {
        return new Itr();
    }

    public class Itr implements Iterator<InnodbUserRecord> {
        InnodbUserRecord cursor = getUserRecordByOffset(infimum.nextRecordOffset() + infimum.offset());

        @Override
        public boolean hasNext() {
            return cursor != InnoDbPage.this.supremum;
        }

        @Override
        public InnodbUserRecord next() {
            if (cursor == supremum) {
                throw new NoSuchElementException();
            }
            InnodbUserRecord result = cursor;
            cursor = getUserRecordByOffset(cursor.nextRecordOffset() + cursor.offset());
            return result;
        }
    }

    @Data
    public static class PageExt {
        InnodbIndex belongIndex;
        IndexPage parent;
    }

}
