package tech.insight.engine.innodb.factory

import tech.insight.engine.innodb.page.ConstantSize
import tech.insight.engine.innodb.page.compact.RecordHeader
import tech.insight.engine.innodb.page.compact.RecordType

/**
 * @author gongxuanzhangmelt@gmail.com
 */
object RecordHeaderFactory {
    fun indexHeader(): RecordHeader {
        val recordHeader = RecordHeader()
        recordHeader.setRecordType(RecordType.PAGE)
        recordHeader.setHeapNo(1)
        recordHeader.setDelete(false)
        recordHeader.setNOwned(1)
        recordHeader.setNextRecordOffset(0)
        return recordHeader
    }

    fun infimumHeader(): RecordHeader {
        val recordHeader = RecordHeader()
        recordHeader.setRecordType(RecordType.INFIMUM)
        recordHeader.setHeapNo(1)
        recordHeader.setDelete(false)
        recordHeader.setNOwned(1)
        recordHeader.setNextRecordOffset(ConstantSize.INFIMUM.size())
        return recordHeader
    }

    fun supremumHeader(): RecordHeader {
        val recordHeader = RecordHeader()
        recordHeader.setRecordType(RecordType.SUPREMUM)
        recordHeader.setHeapNo(1)
        recordHeader.setDelete(false)
        recordHeader.setNOwned(1)
        recordHeader.setNextRecordOffset(0)
        return recordHeader
    }
}
