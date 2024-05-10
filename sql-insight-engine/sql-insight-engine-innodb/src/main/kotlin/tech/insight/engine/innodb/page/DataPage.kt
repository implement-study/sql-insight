package tech.insight.engine.innodb.page

import tech.insight.core.bean.Column
import tech.insight.engine.innodb.index.InnodbIndex
import tech.insight.engine.innodb.page.compact.IndexRecord
import tech.insight.engine.innodb.page.compact.RowFormatFactory
import tech.insight.engine.innodb.page.type.PageType
import tech.insight.engine.innodb.utils.RowComparator


/**
 * @author gongxuanzhangmelt@gmail.com
 */
class DataPage(index: InnodbIndex) : InnoDbPage(index) {




    /**
     * data page will split when free space less than one-sixteenth page size
     */
    override fun pageSplitIfNecessary() {
        if (this.freeSpace.toInt() > ConstantSize.PAGE.size() shr 4) {
            return
        }
        val pageUserRecord: MutableList<InnodbUserRecord> = ArrayList(
            pageHeader.recordCount + 1
        )
        var base: InnodbUserRecord = infimum
        var allLength = 0
        while (true) {
            base = getUserRecordByOffset(base.offset() + base.nextRecordOffset())
            if (base === supremum) {
                break
            }
            pageUserRecord.add(base)
            allLength += base.length()
        }
        //   todo if not middle split ?
        if (pageHeader.directionCount < Constant.DIRECTION_COUNT_THRESHOLD) {
            middleSplit(pageUserRecord, allLength)
        }
    }



    override fun compare(o1: InnodbUserRecord, o2: InnodbUserRecord): Int {
        return RowComparator.primaryKeyComparator().compare(o1, o2)
    }

    companion object {


    }
}
