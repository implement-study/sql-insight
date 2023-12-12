package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.factory;

import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.ConstantSize;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.PageHeader;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public abstract class PageHeaderFactory {


    /**
     * root node file header
     **/
    public static PageHeader rootPageHeader() {
        PageHeader pageHeader = new PageHeader();
        pageHeader.setSlotCount((short) 2);
        pageHeader.setHeapTop(rootHeapTop());
        pageHeader.setAbsoluteRecordCount((short) 2);
        pageHeader.setRecordCount((short) 0);
        pageHeader.setFree((short) 0);
        pageHeader.setGarbage((short) 0);
        pageHeader.setLastInsertOffset(rootHeapTop());
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
     * root heap top offset
     **/
    private static short rootHeapTop() {
        return (short) (ConstantSize.FILE_HEADER.getSize() +
                ConstantSize.PAGE_HEADER.getSize() +
                ConstantSize.INFIMUM.getSize() +
                ConstantSize.SUPREMUM.getSize());
    }

}
