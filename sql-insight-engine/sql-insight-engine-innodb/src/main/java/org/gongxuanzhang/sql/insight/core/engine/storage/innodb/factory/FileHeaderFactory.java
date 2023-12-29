package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.factory;

import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.ConstantSize;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.FileHeader;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.PageType;

import java.nio.ByteBuffer;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public abstract class FileHeaderFactory {


    public static FileHeader readFileHeader(byte[] arr) {
        ConstantSize.FILE_HEADER.checkSize(arr);
        FileHeader fileHeader = new FileHeader();
        ByteBuffer buffer = ByteBuffer.wrap(arr);
        fileHeader.setCheckSum(buffer.getInt());
        fileHeader.setOffset(buffer.getInt());
        fileHeader.setPageType(buffer.getShort());
        fileHeader.setPre(buffer.getInt());
        fileHeader.setNext(buffer.getInt());
        fileHeader.setLsn(buffer.getLong());
        fileHeader.setFlushLsn(buffer.getLong());
        fileHeader.setSpaceId(buffer.getInt());
        return fileHeader;
    }

    /**
     * create a empty file header
     **/
    public static FileHeader createFileHeader() {
        FileHeader fileHeader = new FileHeader();
        fileHeader.setNext(0);
        fileHeader.setPre(0);
        fileHeader.setOffset(0);
        fileHeader.setPageType(PageType.FIL_PAGE_INDEX.getValue());
        return fileHeader;
    }
}

