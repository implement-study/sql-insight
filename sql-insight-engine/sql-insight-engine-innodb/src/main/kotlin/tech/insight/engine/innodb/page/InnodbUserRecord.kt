package tech.insight.engine.innodb.page

import tech.insight.core.bean.UserRecord
import tech.insight.engine.innodb.page.compact.RecordHeader

/**
 * @author gongxuanzhangmelt@gmail.com
 */
interface InnodbUserRecord : UserRecord, PageObject {
    /**
     * @return record header
     */
    val recordHeader: RecordHeader

    /**
     * next record offset is relative offset.
     * offset is after header.
     */
    fun beforeSplitOffset(): Int
}
