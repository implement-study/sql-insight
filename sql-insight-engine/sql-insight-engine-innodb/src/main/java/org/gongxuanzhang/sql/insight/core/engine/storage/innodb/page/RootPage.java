package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page;

import lombok.extern.slf4j.Slf4j;


/**
 * root page.
 * always top of table space.
 * it's different from normal Innodb page is have some manipulate like find insert update.
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
@Slf4j
public class RootPage extends InnoDbPage {


    private IndexPage indexDelegate;

    private DataPage dataDelegate;

    public RootPage(IndexPage indexDelegate) {
        super(indexDelegate.ext.belongIndex);
        this.indexDelegate = indexDelegate;
    }

    public RootPage(DataPage dataDelegate) {
        super(dataDelegate.ext.belongIndex);
        this.dataDelegate = dataDelegate;
    }


    @Override
    public void insertData(InnodbUserRecord data) {
        if (this.dataDelegate != null) {
            this.dataDelegate.insertData(data);
        } else {
            this.indexDelegate.insertData(data);
        }
    }

    @Override
    protected InnodbUserRecord wrapUserRecord(int offsetInPage) {
        return null;
    }

    @Override
    protected void splitIfNecessary() {

    }

//    public void insertData(Compact compact) {
//        if (this.pageType() == PageType.FIL_PAGE_INDEX) {
//            if (this.isEnough(compact.length())) {
//                int targetSlot = findTargetSlot(compact);
//                InnodbUserRecord pre = getUserRecordByOffset(pageDirectory.indexSlot(targetSlot - 1));
//                InnodbUserRecord next = getNextUserRecord(this, pre);
//                while (compact.compareTo(next) > 0) {
//                    pre = next;
//                    next = getNextUserRecord(this, pre);
//                }
//                linkedInsertRow(pre, compact, next);
//                return;
//            }
//            splitDataToIndexPage(compact);
//        } else if (this.pageType() == PageType.FIL_PAGE_INODE) {
//            //   找到目标位置 然后插入
//        } else {
//            throw new UnsupportedOperationException("error type " + this.pageType());
//        }
//    }


}
