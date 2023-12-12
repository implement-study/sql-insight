package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.factory;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.FileTrailer;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.Infimum;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.InnoDbPage;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.PageDirectory;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.Supremum;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.UserRecords;
import org.gongxuanzhang.sql.insight.core.exception.RuntimeIoException;
import org.gongxuanzhang.sql.insight.core.object.Table;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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

    private static InnoDbPage createRoot() {
        InnoDbPage innoDbPage = new InnoDbPage();
        innoDbPage.setFileHeader(FileHeaderFactory.rootFileHeader());
        innoDbPage.setPageHeader(PageHeaderFactory.rootPageHeader());
        innoDbPage.setInfimum(new Infimum());
        innoDbPage.setSupremum(new Supremum());
        innoDbPage.setUserRecords(new UserRecords());
        innoDbPage.setFreeSpace(innoDbPage.getPageHeader().getHeapTop());
        //   todo page directory
        innoDbPage.setPageDirectory(new PageDirectory());
        innoDbPage.setFileTrailer(new FileTrailer());
        return innoDbPage;
    }
}
