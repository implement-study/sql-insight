package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.factory;

import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.ConstantSize;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.PageHeader;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public abstract class PageHeaderFactory {


    /**
     * create a empty page header
     **/
    public static PageHeader createPageHeader() {
        PageHeader pageHeader = new PageHeader();
        pageHeader.setSlotCount((short) 2);
        pageHeader.setHeapTop(createHeapTop());
        pageHeader.setAbsoluteRecordCount((short) 2);
        pageHeader.setRecordCount((short) 0);
        pageHeader.setFree((short) 0);
        pageHeader.setGarbage((short) 0);
        pageHeader.setLastInsertOffset(createHeapTop());
        pageHeader.setLevel((short) 0);
        pageHeader.setDirection((short) 0);
        pageHeader.setDirectionCount((short) 0);
        pageHeader.setMaxTransactionId(0L);
        pageHeader.setIndexId(0);
        pageHeader.setSegLeaf(0L);
        pageHeader.setSegTop(0L);
        return pageHeader;
    }


    /**
     * create heap top offset
     **/
    private static short createHeapTop() {
        return (short) (ConstantSize.FILE_HEADER.size() +
                ConstantSize.PAGE_HEADER.size() +
                ConstantSize.INFIMUM.size() +
                ConstantSize.SUPREMUM.size());
    }

}
