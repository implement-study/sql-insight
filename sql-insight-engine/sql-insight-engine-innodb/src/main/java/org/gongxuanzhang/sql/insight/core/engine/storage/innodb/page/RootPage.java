package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page;

import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.Compact;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.RecordHeader;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.RowFormatFactory;
import org.gongxuanzhang.sql.insight.core.object.InsertRow;
import org.gongxuanzhang.sql.insight.core.object.Table;

import static org.gongxuanzhang.sql.insight.core.engine.storage.innodb.utils.PageSupport.getNextUserRecord;


/**
 * root page.
 * always top of table space.
 * it's different from normal Innodb page is have some manipulate like find insert update.
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public class RootPage extends InnoDbPage {


    public RootPage(Table table) {
        super(table);
    }

    /**
     * insert row to page.
     * <p>
     * page will split if space dont enough.
     **/
    @Override
    public void insertRow(InsertRow row) {
        Compact compact = RowFormatFactory.compactFromInsertRow(row);
        if (this.pageType() == PageType.FIL_PAGE_INODE) {
            //   todo  how to define enough?
            if (this.freeSpace > compact.length()) {
                int targetSlot = findTargetSlot(compact);
                InnodbUserRecord pre = getUserRecordByOffset(pageDirectory.indexSlot(targetSlot), row.belongTo());
                InnodbUserRecord next = getNextUserRecord(this, pre);
                while (compact.compareTo(next) > 0) {
                    pre = next;
                    next = getNextUserRecord(this, pre);
                }
                linkedInsertRow(pre, compact, next);
                adjustGroup(targetSlot,compact);
                return;
            }
            splitPage();
            insertRow(row);
            return;
        }
        if (this.pageType() == PageType.FIL_PAGE_INDEX) {
            InnoDbPage targetPage = findTargetPage(compact);
            targetPage.insertRow(row);
        }
//        if (!isEnough(row.length())) {
//            throw new MySQLException("页选择异常");
//        }
//        Compact insertCompact = row.toUserRecord(Compact.class);
//        int insertSlot = findInsertSlot(insertCompact);
//        //  插入链表
//        short preOffset = this.pageDirectory.getSlots()[insertSlot - 1];
//        UserRecord preGroupMax = getUserRecordByOffset(this, preOffset);
//        insertLinkedList(insertCompact, preGroupMax);
//        //  调整组
//        UserRecord insertGroupMax = getUserRecordByOffset(this, pageDirectory.getSlots()[insertSlot]);
//        int currentOwned = insertGroupMax.getRecordHeader().getNOwned() + 1;
//        insertGroupMax.getRecordHeader().setnOwned(currentOwned);
//        if (currentOwned > Constant.RECORD_SPLIT_SIZE) {
//            groupSplit(insertSlot);
//            this.freeSpace -= 2;
//        }
//        this.refresh();
    }

    private void adjustGroup(int targetSlot, Compact compact) {

    }

    private InnoDbPage findTargetPage(Compact compact) {
        if (this.pageType() == PageType.FIL_PAGE_INODE) {
            if (this.freeSpace < compact.length()) {

            }
        }
        return null;
    }

    private void linkedInsertRow(InnodbUserRecord pre, Compact insertCompact, InnodbUserRecord next) {
        RecordHeader insertHeader = new RecordHeader();
        insertHeader.setHeapNo(this.pageHeader.absoluteRecordCount);
        insertHeader.setNextRecordOffset(next.offset());
        pre.getRecordHeader().setNextRecordOffset(insertCompact.offset());
        insertCompact.setRecordHeader(insertHeader);
        //  adjust page
        this.userRecords.addRecords(insertCompact);
        this.pageHeader.absoluteRecordCount++;
        this.pageHeader.recordCount++;
        this.freeSpace -= (short) insertCompact.length();
        this.pageHeader.heapTop += (short) insertCompact.length();
        this.pageHeader.lastInsertOffset += (short) insertCompact.length();
    }


    @Override
    public void splitPage() {

    }
}
