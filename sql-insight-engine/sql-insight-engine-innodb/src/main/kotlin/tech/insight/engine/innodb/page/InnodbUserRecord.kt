package tech.insight.engine.innodb.page

import tech.insight.core.bean.Row
import tech.insight.core.bean.UserRecord
import tech.insight.core.bean.value.Value
import tech.insight.engine.innodb.index.InnodbIndex
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

    /**
     * a user record shift to index node
     */
    fun indexKey(): Array<Value<*>>

    /**
     * innodb user record must belong to a index
     */
    fun belongIndex(): InnodbIndex


    /**
     * a user record up to index node
     */
    fun indexNode(): InnodbUserRecord

    /**
     * remove myself from page
     */
    fun remove()

    /**
     * @return record that has the max value in group,that is nOwn not 0,may be is myself
     */
    fun groupMax(): InnodbUserRecord

    /**
     * @return next record in page
     */
    fun nextRecord(): InnodbUserRecord

    /**
     * @return pre record in page
     */
    fun preRecord(): InnodbUserRecord

    /**
     * @return this record is group max record,in other words record header n_own is not zero
     */
    fun isGroupMax(): Boolean {
        return this.recordHeader.nOwned != 0
    }

    fun linkRecord(nextRecord: InnodbUserRecord) {
        this.recordHeader.nextRecordOffset = nextRecord.absoluteOffset() - this.absoluteOffset()
    }

    override fun compareTo(other: Row): Int {
        if (other is SystemUserRecord) {
            return -other.compareTo(this)
        }
        if (other is InnodbUserRecord) {
            require(other.belongIndex() == belongIndex()) {
                "The two rows to be compared must belong to the same index"
            }
            val aKey = indexKey()
            val bKey = other.indexKey()
            for (i in aKey.indices) {
                val compare: Int = aKey[i].compareTo(bKey[i])
                if (compare != 0) {
                    return compare
                }
            }
            return 0
        }
        return super.compareTo(other)
    }
}
