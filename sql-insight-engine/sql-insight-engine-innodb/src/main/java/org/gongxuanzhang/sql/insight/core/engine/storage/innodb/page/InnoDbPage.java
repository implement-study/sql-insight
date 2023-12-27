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
import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.easybyte.core.ByteWrapper;
import org.gongxuanzhang.easybyte.core.DynamicByteBuffer;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.factory.PageFactory;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.index.InnodbIndex;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.RecordHeader;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.RecordType;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.utils.PageSupport;
import org.gongxuanzhang.sql.insight.core.exception.DuplicationPrimaryKeyException;

import java.lang.reflect.Field;
import java.util.Comparator;

import static org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.Constant.SLOT_MAX_COUNT;

/**
 * InnoDb Page
 * size default 16K.
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/

@Data
@Slf4j
public abstract class InnoDbPage implements ByteWrapper, Comparator<InnodbUserRecord> {


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

    PageExt ext;

    protected InnoDbPage(InnodbIndex index) {
        this.ext = new PageExt();
        this.ext.belongIndex = index;
    }

    @Override
    public byte[] toBytes() {
        if (ext.source != null) {
            return ext.source;
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
        this.ext.source = buffer.toBytes();
        return this.ext.source;
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
    public abstract void insertData(InnodbUserRecord data);


    /**
     * find the slot where the target record is located
     *
     * @return result must be greater than 0 because 0 only contains infimum, but the slot may be already full
     **/
    protected int findTargetSlot(InnodbUserRecord userRecord) {
        int left = 0;
        int right = pageDirectory.slotCount() - 1;
        while (left < right - 1) {
            int mid = (right + left) / 2;
            short offset = this.pageDirectory.getSlots()[mid];
            InnodbUserRecord base = getUserRecordByOffset(offset);
            int compare = userRecord.compareTo(base);
            if (compare == 0) {
                throw new DuplicationPrimaryKeyException(base.getRowId());
            }
            if (compare < 0) {
                right = mid;
            } else {
                left = mid;
            }
        }
        InnodbUserRecord base = getUserRecordByOffset(this.pageDirectory.getSlots()[left]);
        int compare = userRecord.compareTo(base);
        if (compare == 0) {
            throw new DuplicationPrimaryKeyException(base.getRowId());
        }
        if (compare < 0) {
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
     * free space if enough
     * default implementation is always retain one-sixteenth page size
     *
     * @param insertLength insert length
     **/
    @Deprecated
    public boolean isEnough(int insertLength) {
        return this.freeSpace - insertLength >= ConstantSize.PAGE.size() >> 4;
    }


    /**
     * this page should split.
     * in general after insert row call this method
     **/
    protected abstract void splitIfNecessary();

    protected void linkedAndAdjust(InnodbUserRecord pre, InnodbUserRecord insertRecord, InnodbUserRecord next) {
        RecordHeader insertHeader = insertRecord.getRecordHeader();
        insertHeader.setHeapNo(this.pageHeader.absoluteRecordCount);
        insertRecord.setOffset(this.pageHeader.lastInsertOffset + insertRecord.beforeSplitOffset());
        insertHeader.setNextRecordOffset(next.offset() - insertRecord.offset());
        pre.getRecordHeader().setNextRecordOffset(insertRecord.offset() - pre.offset());
        insertHeader.setRecordType(RecordType.NORMAL);

        //  adjust page
        this.userRecords.addRecord(insertRecord);
        this.pageHeader.absoluteRecordCount++;
        this.pageHeader.recordCount++;
        this.freeSpace -= (short) insertRecord.length();
        this.pageHeader.heapTop += (short) insertRecord.length();
        this.pageHeader.lastInsertOffset += (short) insertRecord.length();


        //  adjust group
        while (next.getRecordHeader().getNOwned() == 0) {
            next = getUserRecordByOffset(next.offset() + next.nextRecordOffset());
        }
        RecordHeader recordHeader = next.getRecordHeader();
        int groupCount = recordHeader.getNOwned();
        recordHeader.setNOwned(groupCount + 1);
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
    public void transferFrom(InnoDbPage page){
        InnoDbPage snapshot = PageFactory.swap(page.ext.source, this.ext.belongIndex);
        this.fileHeader = snapshot.fileHeader;
        this.pageHeader = snapshot.pageHeader;
        this.infimum = snapshot.infimum;
        this.supremum = snapshot.supremum;
        this.userRecords = snapshot.userRecords;
        this.freeSpace = snapshot.freeSpace;
        this.pageDirectory = snapshot.pageDirectory;
        this.fileTrailer = snapshot.fileTrailer;
        this.ext = snapshot.ext;
    }



    @Data
    public static class PageExt {
        byte[] source;
        InnodbIndex belongIndex;
        IndexPage parent;
    }


}
