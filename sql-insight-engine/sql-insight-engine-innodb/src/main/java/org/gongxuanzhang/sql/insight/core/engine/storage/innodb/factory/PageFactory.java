package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.factory;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.easybyte.core.DynamicByteBuffer;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.ConstantSize;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.FileTrailer;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.Infimum;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.InnoDbPage;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.PageDirectory;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.RootPage;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.Supremum;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.UserRecords;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.RecordHeader;
import org.gongxuanzhang.sql.insight.core.exception.RuntimeIoException;
import org.gongxuanzhang.sql.insight.core.object.Table;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.ConstantSize.FILE_HEADER;
import static org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.ConstantSize.INFIMUM;
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
        File primaryFile = new File(table.getDatabase().getDbFolder(), table.getName() + ".idb");
        try {
            if (!primaryFile.createNewFile()) {
                log.warn("{} already exists , execute create table will overwrite file", primaryFile.getAbsoluteFile());
            }
            InnoDbPage root = createRoot();
            Files.write(primaryFile.toPath(), root.toBytes());
        } catch (IOException e) {
            throw new RuntimeIoException(e);
        }
    }

    /**
     * swap byte array to page
     **/
    public static InnoDbPage swap(byte[] bytes) {
        InnoDbPage bean = new RootPage();
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

    private static RootPage createRoot() {
        RootPage root = new RootPage();
        root.setFileHeader(FileHeaderFactory.rootFileHeader());
        root.setPageHeader(PageHeaderFactory.rootPageHeader());
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
