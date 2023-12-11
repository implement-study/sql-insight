package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.factory;

import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.FileHeader;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public abstract class FileHeaderFactory {

    public static FileHeader init(){
        FileHeader fileHeader = new FileHeader();
        return fileHeader;
    }
}
