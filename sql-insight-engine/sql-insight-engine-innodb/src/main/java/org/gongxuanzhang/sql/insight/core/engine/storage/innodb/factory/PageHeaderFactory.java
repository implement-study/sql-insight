package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.factory;

import tech.insight.engine.innodb.page.ConstantSize;
import tech.insight.engine.innodb.page.PageHeader;

import java.nio.ByteBuffer;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public abstract class PageHeaderFactory {

    public static final short EMPTY_PAGE_HEAP_TOP = (short) (ConstantSize.FILE_HEADER.size() +
            ConstantSize.PAGE_HEADER.size() +
            ConstantSize.INFIMUM.size() +
            ConstantSize.SUPREMUM.size());

    /**
     * create a empty page header
     **/
    public static PageHeader createPageHeader() {
        PageHeader pageHeader = new PageHeader();
        pageHeader.setSlotCount((short) 2);
        pageHeader.setHeapTop(EMPTY_PAGE_HEAP_TOP);
        pageHeader.setAbsoluteRecordCount((short) 2);
        pageHeader.setRecordCount((short) 0);
        pageHeader.setFree((short) 0);
        pageHeader.setGarbage((short) 0);
        pageHeader.setLastInsertOffset(EMPTY_PAGE_HEAP_TOP);
        pageHeader.setLevel((short) 0);
        pageHeader.setDirection((short) 0);
        pageHeader.setDirectionCount((short) 0);
        pageHeader.setMaxTransactionId(0L);
        pageHeader.setIndexId(0);
        pageHeader.setSegLeafPre((short) 0);
        pageHeader.setSegLeaf(0L);
        pageHeader.setSegTopPre((short) 0);
        pageHeader.setSegTop(0L);
        return pageHeader;
    }

    public static PageHeader readPageHeader(byte[] pageHeaderArr) {
        ConstantSize.PAGE_HEADER.checkSize(pageHeaderArr);
        ByteBuffer buffer = ByteBuffer.wrap(pageHeaderArr);
        PageHeader pageHeader = new PageHeader();
        pageHeader.setSlotCount(buffer.getShort());
        pageHeader.setHeapTop(buffer.getShort());
        pageHeader.setAbsoluteRecordCount(buffer.getShort());
        pageHeader.setRecordCount(buffer.getShort());
        pageHeader.setFree(buffer.getShort());
        pageHeader.setGarbage(buffer.getShort());
        pageHeader.setLastInsertOffset(buffer.getShort());
        pageHeader.setDirection(buffer.getShort());
        pageHeader.setDirectionCount(buffer.getShort());
        pageHeader.setMaxTransactionId(buffer.getLong());
        pageHeader.setLevel(buffer.getShort());
        pageHeader.setIndexId(buffer.getLong());
        pageHeader.setSegLeafPre(buffer.getShort());
        pageHeader.setSegLeaf(buffer.getLong());
        pageHeader.setSegTopPre(buffer.getShort());
        pageHeader.setSegTop(buffer.getLong());
        return pageHeader;
    }


}
