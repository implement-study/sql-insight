package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.Compact;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.RecordHeader;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.utils.RowComparator;
import org.gongxuanzhang.sql.insight.core.object.Table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.Constant.DIRECTION_COUNT_THRESHOLD;
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

    @Override
    public void insertData(Compact compact) {
        if (this.pageType() == PageType.FIL_PAGE_INDEX) {
            if (this.isEnough(compact.length())) {
                int targetSlot = findTargetSlot(compact);
                InnodbUserRecord pre = getUserRecordByOffset(pageDirectory.indexSlot(targetSlot - 1));
                InnodbUserRecord next = getNextUserRecord(this, pre);
                while (compact.compareTo(next) > 0) {
                    pre = next;
                    next = getNextUserRecord(this, pre);
                }
                linkedInsertRow(pre, compact, next);
                return;
            }
            splitDataToIndexPage(compact);
        } else if (this.pageType() == PageType.FIL_PAGE_INODE) {
            //   找到目标位置 然后插入
        } else {
            throw new UnsupportedOperationException("error type " + this.pageType());
        }
    }


    /**
     * root page split and type to index from data type.
     **/
    private void splitDataToIndexPage(Compact insertCompact) {
        List<InnodbUserRecord> pageUserRecord = new ArrayList<>(this.pageHeader.recordCount + 1);
        InnodbUserRecord base = this.infimum;
        Comparator<InnodbUserRecord> comparator = RowComparator.primaryKeyComparator();
        boolean inserted = false;
        int allLength = 0;
        while (base != this.supremum) {
            base = getNextUserRecord(this, base);
            if (!inserted && comparator.compare(insertCompact, base) < 0) {
                pageUserRecord.add(insertCompact);
                allLength += insertCompact.length();
                inserted = true;
            }
            pageUserRecord.add(base);
            allLength += base.length();
        }
        //   todo non middle split ?
        if (this.pageHeader.directionCount < DIRECTION_COUNT_THRESHOLD) {
            middleSplit(pageUserRecord, allLength);
        }

    }


    /**
     * middle split.
     * insert direction unidentified (directionCount less than 5)
     *
     * @param pageUserRecord all user record in page with inserted
     * @param allLength      all user record length
     **/
    private void middleSplit(List<InnodbUserRecord> pageUserRecord, int allLength) {
        int half = allLength / 2;
        for (int i = 0; i < pageUserRecord.size(); i++) {
            allLength -= pageUserRecord.get(i).length();
            if (allLength <= half) {

                List<InnodbUserRecord> preData = pageUserRecord.subList(0, i);
                List<InnodbUserRecord> nextData = pageUserRecord.subList(i, pageUserRecord.size());
            }
        }
    }


    private void linkedInsertRow(InnodbUserRecord pre, Compact insertCompact, InnodbUserRecord next) {
        RecordHeader insertHeader = new RecordHeader();
        insertHeader.setHeapNo(this.pageHeader.absoluteRecordCount);
        insertHeader.setNextRecordOffset(next.offset() + insertCompact.getBody().length);
        pre.getRecordHeader().setNextRecordOffset(insertCompact.offset() );
        insertCompact.setRecordHeader(insertHeader);
        //  adjust page
        this.userRecords.addRecord(insertCompact);
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
        recordHeader.setNOwned(groupCount + 1);
        if (next.getRecordHeader().getNOwned() > SLOT_MAX_COUNT) {
            log.info("start group split ...");
            for (int i = 0; i < this.pageDirectory.slots.length - 1; i++) {
                if (this.pageDirectory.slots[i] == next.offset()) {
                    InnodbUserRecord preGroupMax = getUserRecordByOffset(this.pageDirectory.slots[i + 1]);
                    for (int j = 0; j < SLOT_MAX_COUNT >> 1; j++) {
                        preGroupMax = getNextUserRecord(this, preGroupMax);
                    }
                    this.pageDirectory.split(i, (short) preGroupMax.offset());
                }
            }
            log.info("end group split ...");
        }
    }


}
