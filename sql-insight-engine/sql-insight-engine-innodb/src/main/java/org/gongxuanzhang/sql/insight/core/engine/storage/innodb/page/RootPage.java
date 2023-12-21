package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.Compact;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.RecordHeader;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.RowFormatFactory;
import org.gongxuanzhang.sql.insight.core.object.InsertRow;
import org.gongxuanzhang.sql.insight.core.object.Table;

import static org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.Constant.SLOT_MAX_COUNT;
import static org.gongxuanzhang.sql.insight.core.engine.storage.innodb.utils.PageSupport.getNextUserRecord;


/**
 * root page.
 * always top of table space.
 * it's different from normal Innodb page is have some manipulate like find insert update.
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
@Slf4j
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

        //  adjust group
        while (next.getRecordHeader().getNOwned() == 0) {
            next = getNextUserRecord(this, next);
        }
        RecordHeader recordHeader = next.getRecordHeader();
        int groupCount = recordHeader.getNOwned();
        recordHeader.setnOwned(groupCount + 1);
        if (next.getRecordHeader().getNOwned() > SLOT_MAX_COUNT) {
            log.info("start group split ...");
            for (int i = 0; i < this.pageDirectory.slots.length - 1; i++) {
                if (this.pageDirectory.slots[i] == next.offset()) {
                    InnodbUserRecord preGroupMax = getUserRecordByOffset(this.pageDirectory.slots[i + 1], this.table);
                    for (int j = 0; j < SLOT_MAX_COUNT >> 1; j++) {
                        preGroupMax = getNextUserRecord(this, preGroupMax);
                    }
                    this.pageDirectory.split(i, (short) preGroupMax.offset());
                }
            }
        }
    }


    @Override
    public void splitPage() {

    }
}
