package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page;

import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.Compact;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.RowFormatFactory;
import org.gongxuanzhang.sql.insight.core.object.InsertRow;


/**
 * root page.
 * always top of table space.
 * it's different from normal Innodb page is have some manipulate like find insert update.
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public class RootPage extends InnoDbPage {

    /**
     * insert row to page.
     * <p>
     * page will split if space dont enough.
     **/
    @Override
    public void insertRow(InsertRow row) {
        Compact compact = RowFormatFactory.fromInsertRow(row);
        if (this.pageType() == PageType.FIL_PAGE_INODE) {
            if (this.freeSpace > compact.length()) {
                //  插入
                //  拿到目标槽位
                //  
                return;
            }
            splitPage();
            insertRow(row);
            return;
        }
        if(this.pageType() == PageType.FIL_PAGE_INDEX){
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

    private void dataInsert(InsertRow row, InnoDbPage rootPage) {

    }


    @Override
    public void splitPage() {

    }
}
