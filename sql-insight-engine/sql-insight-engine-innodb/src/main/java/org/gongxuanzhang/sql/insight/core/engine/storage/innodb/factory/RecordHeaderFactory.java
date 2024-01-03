package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.factory;

import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.ConstantSize;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.RecordHeader;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.RecordType;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public abstract class RecordHeaderFactory {

    public static RecordHeader indexHeader() {
        RecordHeader recordHeader = new RecordHeader();
        recordHeader.setRecordType(RecordType.PAGE);
        recordHeader.setHeapNo(1);
        recordHeader.setDelete(false);
        recordHeader.setNOwned(1);
        recordHeader.setNextRecordOffset(0);
        return recordHeader;
    }

    public static RecordHeader infimumHeader() {
        RecordHeader recordHeader = new RecordHeader();
        recordHeader.setRecordType(RecordType.INFIMUM);
        recordHeader.setHeapNo(1);
        recordHeader.setDelete(false);
        recordHeader.setNOwned(1);
        recordHeader.setNextRecordOffset(ConstantSize.INFIMUM.size());
        return recordHeader;
    }

    public static RecordHeader supremumHeader() {
        RecordHeader recordHeader = new RecordHeader();
        recordHeader.setRecordType(RecordType.SUPREMUM);
        recordHeader.setHeapNo(1);
        recordHeader.setDelete(false);
        recordHeader.setNOwned(1);
        recordHeader.setNextRecordOffset(0);
        return recordHeader;
    }
}
