package tech.insight.engine.innodb.page

import org.gongxuanzhang.easybyte.core.DynamicByteBuffer
import tech.insight.core.bean.Row
import tech.insight.core.bean.Table
import tech.insight.core.bean.value.Value
import tech.insight.engine.innodb.index.InnodbIndex
import tech.insight.engine.innodb.page.compact.RecordHeader
import tech.insight.engine.innodb.page.compact.RecordType


/**
 * max record in group
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 */

sealed interface SystemUserRecord : InnodbUserRecord {

    override val values: List<Value<*>>
        get() = systemUserRecordUnsupported()
    override val rowId: Long
        get() = systemUserRecordUnsupported()

    override fun getValueByColumnName(colName: String): Value<*> {
        systemUserRecordUnsupported()
    }

    override fun setAbsoluteOffset(offset: Int) {
        throw UnsupportedOperationException("infimum and supremum can't set offset ")
    }

    override fun belongTo(): Table {
        throw UnsupportedOperationException("infimum and supremum only use to support b-tree search")
    }

    override fun deleteSign(): Boolean {
        return false
    }

    override fun indexKey(): Array<Value<*>> {
        systemUserRecordUnsupported()
    }

    private fun systemUserRecordUnsupported(): Nothing {
        throw UnsupportedOperationException("infimum and supremum not support operator!")
    }
}

class Supremum private constructor(private val belongToIndex: InnodbIndex) : SystemUserRecord {

    /**
     * 5 bytes
     */
    override lateinit var recordHeader: RecordHeader

    /**
     * 8 bytes as "supremum"
     */
    private val body = SUPREMUM_BODY.toByteArray()

    override fun rowBytes(): ByteArray {
        return DynamicByteBuffer.wrap(recordHeader.toBytes()).append(body).toBytes()
    }

    override fun absoluteOffset(): Int {
        return ConstantSize.SUPREMUM.offset()
    }


    /**
     * supremum next is infimum
     */
    override fun nextRecordOffset(): Int {
        return 0
    }

    override fun beforeSplitOffset(): Int {
        return recordHeader.length()
    }

    override fun belongIndex(): InnodbIndex {
        return belongToIndex
    }

    override fun indexNode(): InnodbUserRecord {
        throw UnsupportedOperationException("this is supremum!")
    }

    override fun length(): Int {
        return ConstantSize.SUPREMUM.size()
    }

    override fun toString(): String {
        return "[body:" + String(body) + "]"
    }

    override operator fun compareTo(other: Row): Int {
        if (other is InnodbUserRecord) {
            return 1
        }
        throw UnsupportedOperationException("this is supremum!")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Supremum

        if (recordHeader != other.recordHeader) return false
        if (!body.contentEquals(other.body)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = recordHeader.hashCode()
        result = 31 * result + body.contentHashCode()
        return result
    }


    companion object {

        const val SUPREMUM_BODY = "supremum"

        fun create(belongToIndex: InnodbIndex) = Supremum(belongToIndex).apply {
            this.recordHeader = RecordHeader.create(RecordType.SUPREMUM)
        }

        fun wrap(bytes: ByteArray, belongToIndex: InnodbIndex) = Supremum(belongToIndex).apply {
            ConstantSize.SUPREMUM.checkSize(bytes)
            val buffer: DynamicByteBuffer = DynamicByteBuffer.wrap(bytes)
            val headBuffer: ByteArray = buffer.getLength(ConstantSize.RECORD_HEADER.size())
            this.recordHeader = RecordHeader.wrap(headBuffer)
        }
    }

}


/**
 * min record in group
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 */
class Infimum private constructor(private val belongToIndex: InnodbIndex) : SystemUserRecord {

    /**
     * 5 bytes.
     */
    override lateinit var recordHeader: RecordHeader

    /**
     * fixed 8 bytes. "infimum" is 7 bytes . fill 0 zero occupy the space
     */
    private val body: ByteArray = DynamicByteBuffer.wrap(INFIMUM_BODY.toByteArray()).append(0.toByte()).toBytes()


    override fun rowBytes(): ByteArray {
        return DynamicByteBuffer.wrap(recordHeader.toBytes()).append(body).toBytes()
    }

    override fun beforeSplitOffset(): Int {
        return recordHeader.length()
    }

    override fun belongIndex(): InnodbIndex {
        return belongToIndex
    }

    override fun indexNode(): InnodbUserRecord {
        throw UnsupportedOperationException("this is infimum!")
    }

    override fun nextRecordOffset(): Int {
        return recordHeader.nextRecordOffset
    }

    override fun absoluteOffset(): Int {
        return ConstantSize.INFIMUM.offset()
    }


    override fun length(): Int {
        return ConstantSize.INFIMUM.size()
    }


    override operator fun compareTo(other: Row): Int {
        if (other is InnodbUserRecord) {
            return -1
        }
        throw UnsupportedOperationException("this is infimum!")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Infimum

        if (recordHeader != other.recordHeader) return false
        if (!body.contentEquals(other.body)) return false

        return true
    }


    override fun hashCode(): Int {
        var result = recordHeader.hashCode()
        result = 31 * result + body.contentHashCode()
        return result
    }

    override fun toString(): String {
        return recordHeader.toString() + "[body:" + String(body) + "]"
    }


    companion object {

        private const val INFIMUM_BODY = "infimum"

        fun create(belongToIndex: InnodbIndex) = Infimum(belongToIndex).apply {
            this.recordHeader = RecordHeader.create(RecordType.INFIMUM)

        }

        fun wrap(bytes: ByteArray, belongToIndex: InnodbIndex) = Infimum(belongToIndex).apply {
            ConstantSize.SUPREMUM.checkSize(bytes)
            val buffer: DynamicByteBuffer = DynamicByteBuffer.wrap(bytes)
            val headBuffer: ByteArray = buffer.getLength(ConstantSize.RECORD_HEADER.size())
            this.recordHeader = RecordHeader.wrap(headBuffer)
        }

    }

}

