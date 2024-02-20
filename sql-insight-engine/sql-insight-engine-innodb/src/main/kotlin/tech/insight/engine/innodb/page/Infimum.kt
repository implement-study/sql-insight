package tech.insight.engine.innodb.page

import org.gongxuanzhang.easybyte.core.DynamicByteBuffer
import tech.insight.core.bean.Row
import tech.insight.core.bean.Table
import tech.insight.core.bean.value.Value
import tech.insight.engine.innodb.factory.RecordHeaderFactory
import tech.insight.engine.innodb.page.compact.RecordHeader


/**
 * min record in group
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 */
class Infimum : InnodbUserRecord {

    companion object {
        private const val INFIMUM_BODY = "infimum"
    }

    /**
     * 5 bytes.
     */
    override var recordHeader: RecordHeader

    /**
     * fixed 8 bytes. "infimum" is 7 bytes . fill 0 zero occupy the space
     */
    val body: ByteArray

    init {
        recordHeader = RecordHeaderFactory.infimumHeader()
        body = DynamicByteBuffer.wrap(INFIMUM_BODY.toByteArray()).append(0.toByte()).toBytes()
    }

    override fun rowBytes(): ByteArray {
        return DynamicByteBuffer.wrap(recordHeader.toBytes()).append(body).toBytes()
    }

    override fun beforeSplitOffset(): Int {
        return recordHeader.length()
    }

    override fun nextRecordOffset(): Int {
        return recordHeader.nextRecordOffset
    }

    override fun deleteSign(): Boolean {
        return false
    }

    override fun offset(): Int {
        return ConstantSize.INFIMUM.offset()
    }

    override fun setOffset(offset: Int) {
        throw UnsupportedOperationException("infimum can't set offset ")
    }

    override fun toString(): String {
        return recordHeader.toString() + "[body:" + String(body) + "]"
    }

    override val values: List<Value<*>>
        get() = infimumUnsupported()
    override val rowId: Long
        get() = infimumUnsupported()

    override fun getValueByColumnName(colName: String): Value<*> {
        infimumUnsupported()
    }

    override fun belongTo(): Table {
        infimumUnsupported()
    }

    override operator fun compareTo(that: Row): Int {
        return if (that is InnodbUserRecord) {
            -1
        } else infimumUnsupported()
    }

    private fun infimumUnsupported(): Nothing {
        throw UnsupportedOperationException("this is infimum!")
    }


    override fun length(): Int {
        return ConstantSize.INFIMUM.size()
    }

    fun setRecordHeader(recordHeader: RecordHeader): Infimum {
        this.recordHeader = recordHeader
        return this
    }


}
