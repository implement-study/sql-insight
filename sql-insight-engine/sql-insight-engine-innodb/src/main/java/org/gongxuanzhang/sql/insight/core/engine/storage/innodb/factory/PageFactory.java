package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.factory;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.easybyte.core.DynamicByteBuffer;
import tech.insight.engine.innodb.index.ClusteredIndex;
import tech.insight.engine.innodb.index.InnodbIndex;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.*;
import tech.insight.engine.innodb.page.*;
import tech.insight.engine.innodb.page.compact.RecordHeader;
import org.gongxuanzhang.sql.insight.core.exception.RuntimeIoException;
import org.gongxuanzhang.sql.insight.core.object.Table;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static tech.insight.engine.innodb.page.ConstantSize.FILE_HEADER;
import static tech.insight.engine.innodb.page.ConstantSize.FILE_TRAILER;
import static tech.insight.engine.innodb.page.ConstantSize.INFIMUM;
import static tech.insight.engine.innodb.page.ConstantSize.PAGE;
import static tech.insight.engine.innodb.page.ConstantSize.PAGE_HEADER;
import static tech.insight.engine.innodb.page.ConstantSize.SUPREMUM;
import static tech.insight.engine.innodb.page.PageType.FIL_PAGE_INDEX;
import static tech.insight.engine.innodb.page.PageType.FIL_PAGE_INODE;

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
    public static DataPage createDataPage(List<InnodbUserRecord> recordList, InnodbIndex index) {
        DataPage dataPage = new DataPage(index);
        fillInnodbUserRecords(recordList, dataPage);
        dataPage.getFileHeader().setPageType(FIL_PAGE_INDEX.value);
        return dataPage;
    }

    public static IndexPage createIndexPage(List<InnodbUserRecord> indexRecordList, InnodbIndex index) {
        IndexPage indexPage = new IndexPage(index);
        fillInnodbUserRecords(indexRecordList, indexPage);
        indexPage.getFileHeader().setPageType(FIL_PAGE_INODE.value);
        return indexPage;
    }

    private static void fillInnodbUserRecords(List<InnodbUserRecord> recordList, InnoDbPage page) {
        FileHeader fileHeader = new FileHeader();
        page.setFileHeader(fileHeader);

        PageHeader pageHeader = new PageHeader();
        pageHeader.setSlotCount((short) (((recordList.size() + 1) / 8) + 1));
        pageHeader.setAbsoluteRecordCount((short) (2 + recordList.size()));
        pageHeader.setRecordCount((short) recordList.size());
        pageHeader.setLastInsertOffset((short) ConstantSize.USER_RECORDS.offset());

        page.setSupremum(new Supremum());
        page.setInfimum(new Infimum());

        short[] slots = new short[((recordList.size() + 1) / Constant.SLOT_MAX_COUNT) + 1];
        page.setPageDirectory(new PageDirectory(slots));
        page.setFileTrailer(new FileTrailer());
        UserRecords userRecords = new UserRecords();
        page.setUserRecords(userRecords);

        InnodbUserRecord pre = page.getInfimum();
        short preOffset = (short) SUPREMUM.offset();
        for (int i = 0; i < recordList.size(); i++) {
            InnodbUserRecord current = recordList.get(i);
            int currentOffset = pageHeader.getLastInsertOffset() + current.beforeSplitOffset();
            pageHeader.setLastInsertOffset((short) (pageHeader.getLastInsertOffset() + current.length()));
            pre.recordHeader.setNextRecordOffset(currentOffset - preOffset);
            pre = current;
            if ((i + 1) % Constant.SLOT_MAX_COUNT == 0) {
                slots[slots.length - 1 - ((i + 1) % Constant.SLOT_MAX_COUNT)] = (short) currentOffset;
            }
        }
        pre.recordHeader.setNextRecordOffset(SUPREMUM.offset());
        page.setPageDirectory(new PageDirectory(slots));
        slots[0] = (short) SUPREMUM.offset();
        slots[slots.length - 1] = (short) INFIMUM.offset();
        page.setFileTrailer(new FileTrailer());
    }

    public static InnoDbPage findPageByOffset(int pageOffset, InnodbIndex index) {
        File file = index.getFile();
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
            randomAccessFile.seek(pageOffset);
            byte[] pageArr = PAGE.emptyBuff();
            randomAccessFile.readFully(pageArr);
            return swap(pageArr, index);
        } catch (IOException e) {
            throw new RuntimeIoException(e);
        }
    }

    /**
     * swap byte array to page
     **/
    public static InnoDbPage swap(byte[] bytes, InnodbIndex index) {
        ConstantSize.PAGE.checkSize(bytes);

        DynamicByteBuffer buffer = DynamicByteBuffer.wrap(bytes);
        //  file header
        byte[] fileHeaderBytes = buffer.getLength(FILE_HEADER.size());
        FileHeader fileHeader = FileHeaderFactory.readFileHeader(fileHeaderBytes);
        // page header
        byte[] pageHeaderBytes = buffer.getLength(PAGE_HEADER.size());
        PageHeader pageHeader = PageHeaderFactory.readPageHeader(pageHeaderBytes);
        //  infimum
        byte[] infimumBytes = buffer.getLength(INFIMUM.size());
        Infimum infimum = readInfimum(infimumBytes);
        //  supremum
        byte[] supremumBytes = buffer.getLength(SUPREMUM.size());
        Supremum supremum = readSupremum(supremumBytes);
        InnoDbPage result;
        if (fileHeader.getPageType() == FIL_PAGE_INODE.value) {
            result = new IndexPage(index);
        } else {
            result = new DataPage(index);
        }
        result.setFileHeader(fileHeader);
        result.setPageHeader(pageHeader);
        result.setInfimum(infimum);
        result.setSupremum(supremum);
        //   file trailer
        byte[] trailerArr = Arrays.copyOfRange(bytes, bytes.length - FILE_TRAILER.size(), bytes.length);
        result.setFileTrailer(readFileTrailer(trailerArr));
        //   page directory
        int dirOffset = bytes.length - FILE_TRAILER.size() - Short.BYTES;
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        List<Short> shortList = new ArrayList<>();
        short slot = byteBuffer.getShort(dirOffset);
        while (slot != 0) {
            shortList.add(slot);
            dirOffset -= Short.BYTES;
            slot = byteBuffer.getShort(dirOffset);
        }
        short[] slots = new short[shortList.size()];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = shortList.get(shortList.size() - 1 - i);
        }
        result.setPageDirectory(new PageDirectory(slots));
        //  user records
        int bodyLength = pageHeader.getHeapTop() - PageHeaderFactory.EMPTY_PAGE_HEAP_TOP;
        byte[] body = Arrays.copyOfRange(bytes, PageHeaderFactory.EMPTY_PAGE_HEAP_TOP,
                PageHeaderFactory.EMPTY_PAGE_HEAP_TOP + bodyLength);
        UserRecords userRecords = new UserRecords(body);
        result.setUserRecords(userRecords);
        return result;
    }

    private static InnoDbPage createRoot(InnodbIndex index) {
        DataPage root = new DataPage(index);
        root.setFileHeader(FileHeaderFactory.createFileHeader());
        root.setPageHeader(PageHeaderFactory.createPageHeader());
        root.setInfimum(new Infimum());
        root.setSupremum(new Supremum());
        root.setUserRecords(new UserRecords());
        root.setPageDirectory(new PageDirectory());
        root.setFileTrailer(new FileTrailer());
        return root;
    }

    public static Supremum readSupremum(byte[] bytes) {
        ConstantSize.SUPREMUM.checkSize(bytes);
        DynamicByteBuffer buffer = DynamicByteBuffer.wrap(bytes);
        Supremum supremum = new Supremum();
        byte[] headBuffer = buffer.getLength(ConstantSize.RECORD_HEADER.size());
        supremum.setRecordHeader(new RecordHeader(headBuffer));
        return supremum;
    }


    public static Infimum readInfimum(byte[] bytes) {
        INFIMUM.checkSize(bytes);
        DynamicByteBuffer buffer = DynamicByteBuffer.wrap(bytes);
        byte[] headBuffer = buffer.getLength(ConstantSize.RECORD_HEADER.size());
        Infimum infimum = new Infimum();
        infimum.setRecordHeader(new RecordHeader(headBuffer));
        return infimum;
    }

    public static FileTrailer readFileTrailer(byte[] bytes) {
        FILE_TRAILER.checkSize(bytes);
        DynamicByteBuffer buffer = DynamicByteBuffer.wrap(bytes);
        FileTrailer fileTrailer = new FileTrailer();
        fileTrailer.setCheckSum(buffer.getInt());
        fileTrailer.setLsn(buffer.getInt());
        return fileTrailer;
    }
}
