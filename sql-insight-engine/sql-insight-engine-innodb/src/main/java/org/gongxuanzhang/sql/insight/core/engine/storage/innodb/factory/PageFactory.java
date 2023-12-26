package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.factory;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.easybyte.core.DynamicByteBuffer;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.index.ClusteredIndex;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.*;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.RecordHeader;
import org.gongxuanzhang.sql.insight.core.exception.RuntimeIoException;
import org.gongxuanzhang.sql.insight.core.object.Index;
import org.gongxuanzhang.sql.insight.core.object.Table;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.ConstantSize.FILE_HEADER;
import static org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.ConstantSize.FILE_TRAILER;
import static org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.ConstantSize.INFIMUM;
import static org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.ConstantSize.PAGE;
import static org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.ConstantSize.PAGE_HEADER;
import static org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.ConstantSize.SUPREMUM;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
@Slf4j
public abstract class PageFactory {


    /**
     * create idb file and add a root page .
     **/
    public static void initialization(Table table) {
        ClusteredIndex clusteredIndex = new ClusteredIndex(table);
        File primaryFile = clusteredIndex.getFile();
        try {
            if (!primaryFile.createNewFile()) {
                log.warn("{} already exists , execute create table will overwrite file", primaryFile.getAbsoluteFile());
            }
            InnoDbPage root = createRoot(clusteredIndex);
            Files.write(primaryFile.toPath(), root.toBytes());
        } catch (IOException e) {
            throw new RuntimeIoException(e);
        }
    }


    /**
     * page split create a new data page.
     *
     * @param recordList data in the page that sorted
     * @return the data page but the file header , page header is not complete
     **/
    public static DataPage createDataPage(List<InnodbUserRecord> recordList, Index index) {
        DataPage dataPage = new DataPage(index);
        FileHeader fileHeader = new FileHeader();
        fileHeader.setPageType(PageType.FIL_PAGE_INDEX.getValue());
        dataPage.setFileHeader(fileHeader);

        PageHeader pageHeader = new PageHeader();
        pageHeader.setSlotCount((short) (((recordList.size() + 1) / 8) + 1));
        pageHeader.setAbsoluteRecordCount((short) (2 + recordList.size()));
        pageHeader.setRecordCount((short) recordList.size());
        pageHeader.setLastInsertOffset((short) ConstantSize.USER_RECORDS.offset());

        dataPage.setSupremum(new Supremum());
        dataPage.setInfimum(new Infimum());

        short[] slots = new short[((recordList.size() + 1) / Constant.SLOT_MAX_COUNT) + 1];
        dataPage.setPageDirectory(new PageDirectory(slots));
        dataPage.setFileTrailer(new FileTrailer());
        UserRecords userRecords = new UserRecords();
        dataPage.setUserRecords(userRecords);
        //   相对偏移量

        InnodbUserRecord pre = dataPage.getInfimum();
        short preOffset = (short) SUPREMUM.offset();
        for (int i = 0; i < recordList.size(); i++) {
            InnodbUserRecord current = recordList.get(i);
            int currentOffset = pageHeader.getLastInsertOffset() + current.beforeSplitOffset();
            pageHeader.setLastInsertOffset((short) (pageHeader.getLastInsertOffset() + current.length()));
            pre.getRecordHeader().setNextRecordOffset(currentOffset - preOffset);
            pre = current;
            if ((i + 1) % Constant.SLOT_MAX_COUNT == 0) {
                slots[slots.length - 1 - ((i + 1) % Constant.SLOT_MAX_COUNT)] = (short) currentOffset;
            }
        }
        pre.getRecordHeader().setNextRecordOffset(SUPREMUM.offset());
        dataPage.setPageDirectory(new PageDirectory(slots));
        slots[0] = (short) SUPREMUM.offset();
        slots[slots.length - 1] = (short) INFIMUM.offset();
        dataPage.setFileTrailer(new FileTrailer());
        dataPage.setFreeSpace((short) (PAGE.size() - PAGE_HEADER.size() - FILE_HEADER.size() - FILE_TRAILER.size() -
                slots.length * Short.BYTES - userRecords.length()));
        return dataPage;
    }

    /**
     * swap byte array to page
     **/
    public static InnoDbPage swap(byte[] bytes, Index index) {
        InnoDbPage bean = new RootPage(index);
        ConstantSize.PAGE.checkSize(bytes);

        DynamicByteBuffer buffer = DynamicByteBuffer.wrap(bytes);
        //  file header
        byte[] fileHeaderBytes = buffer.getLength(FILE_HEADER.size());
        // page header
        byte[] pageHeaderBytes = buffer.getLength(PAGE_HEADER.size());
        //  infimum
        byte[] infimumBytes = buffer.getLength(INFIMUM.size());
        //  supremum
        byte[] supremumBytes = buffer.getLength(SUPREMUM.size());

        //  user records 使用
//        short heapTop = bean.pageHeader.getHeapTop();
//        int userRecordLength = heapTop - buffer.position();
//        candidate = new byte[userRecordLength];
//        buffer.get(candidate);
//        UserRecordsFactory userRecordsFactory = new UserRecordsFactory();
//        bean.setUserRecords(userRecordsFactory.swap(candidate));
//
//        short slotCount = bean.pageHeader.slotCount;
//        short slotByteLength = (short) (slotCount * 2);
//
//        short freeLength = (short) (buffer.remaining() - slotByteLength - ConstantSize.FILE_TRAILER.getSize());
//        buffer.position(buffer.position() + freeLength);
//        bean.setFreeSpace(freeLength);
//        candidate = new byte[slotByteLength];
//        buffer.get(candidate);
//        PageDirectory pd = new PageDirectoryFactory().swap(candidate);
//        bean.setPageDirectory(pd);
//
//        candidate = new byte[ConstantSize.FILE_TRAILER.getSize()];
//        buffer.get(candidate);
//        FileTrailerFactory trailerFactory = new FileTrailerFactory();
//        FileTrailer fileTrailer = trailerFactory.swap(candidate);
//        bean.setFileTrailer(fileTrailer);
        return bean;
    }

    private static RootPage createRoot(Index index) {
        RootPage root = new RootPage(index);
        root.setFileHeader(FileHeaderFactory.createFileHeader());
        root.setPageHeader(PageHeaderFactory.createPageHeader());
        root.setInfimum(new Infimum());
        root.setSupremum(new Supremum());
        root.setUserRecords(new UserRecords());
        root.setFreeSpace(root.getPageHeader().getHeapTop());
        //   todo page directory
        root.setPageDirectory(new PageDirectory());
        root.setFileTrailer(new FileTrailer());
        return root;
    }

    public static Supremum swapSupremum(byte[] bytes) {
        Supremum supremum = new Supremum();
        ConstantSize.SUPREMUM.checkSize(bytes);
        DynamicByteBuffer buffer = DynamicByteBuffer.wrap(bytes);
        byte[] headBuffer = buffer.getLength(ConstantSize.RECORD_HEADER.size());
        supremum.setRecordHeader(new RecordHeader(headBuffer));
        return supremum;
    }
}
