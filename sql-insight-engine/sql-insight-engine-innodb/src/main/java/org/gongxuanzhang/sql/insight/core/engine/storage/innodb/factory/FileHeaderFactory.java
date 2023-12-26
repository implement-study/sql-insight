package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.factory;

import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.FileHeader;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.PageType;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public abstract class FileHeaderFactory {


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
