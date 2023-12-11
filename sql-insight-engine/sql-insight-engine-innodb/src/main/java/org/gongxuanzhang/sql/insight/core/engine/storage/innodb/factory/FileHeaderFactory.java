package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.factory;

import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.FileHeader;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public abstract class FileHeaderFactory {


    /**
     * create table root page file header
     **/
    public static FileHeader initRootFileHeader() {
        return new FileHeader();
    }
}
