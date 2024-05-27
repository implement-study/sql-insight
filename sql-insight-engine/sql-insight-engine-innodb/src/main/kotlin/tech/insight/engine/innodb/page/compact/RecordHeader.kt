package tech.insight.engine.innodb.page.compact

import org.gongxuanzhang.easybyte.core.ByteWrapper
import tech.insight.core.extension.setBit0
import tech.insight.core.extension.setBit1
import tech.insight.engine.innodb.core.Lengthable
import tech.insight.engine.innodb.page.ConstantSize
import java.nio.ByteBuffer
import kotlin.experimental.and
import kotlin.experimental.or

/**
 * 5 bytes 40 bits.
 * ┌────────────┬────────────┬──────────────┬───────────────┬──────────┬───────────┬──────────────┬───────────────┐
 * │unuseful(1) │ unuseful(1)│delete_mask(1)│min_rec_mask(1)│n_owned(4)│heap_no(13)│record_type(3)│next_record(16)│
 * └────────────┴────────────┴──────────────┴───────────────┴──────────┴───────────┴──────────────┴───────────────┘
 * delete_mask
 * min_rec_mask: whether non-leaf node min record，only index node may be 1
 * n_owned : records count in group. only max record in group have this count.
 * heap_no: the record number in page, infimum is 0, supremum is 1,user record start with 2.
 * record_type:0 normal record 1 non leaf node (index) 2 infimum 3 supremum
 * next_record:next record offset in this page. supremum next_record is 0
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 */
class RecordHeader private constructor() : ByteWrapper, Lengthable {


    private val source: ByteArray = ByteArray(5)
    var delete = false
        set(value) {
            if (field == value) {
                return
            }
            field = value
            if (value) {
                source[0] = source[0].setBit1(5)
            } else {
                source[0] = source[0].setBit0(5)
            }
        }
    var minRec = false
        set(value) {
            if (field == value) {
                return
            }
            field = value
            if (value) {
                source[0] = source[0].setBit1(4)
            } else {
                source[0] = source[0].setBit0(5)
            }
        }

    var nOwned = 0
        set(value) {
            if (field == value) {
                return
            }
            field = value
            source[0] = (source[0].toInt() and 0xF0).toByte()
            source[0] = (source[0].toInt() or value).toByte()
        }
    var heapNo: UInt = 0U
        set(value) {
            if (field == value) {
                return
            }
            field = value
            val buffer = ByteBuffer.allocate(Int.SIZE_BYTES)
            val bytes = buffer.putShort((value.toInt() shl 3).toShort()).array()
            source[1] = bytes[0]
            source[2] = source[2] and 0b00000111.toByte()
            source[2] = source[2] or bytes[1]
        }
    var nextRecordOffset: Short = 0
        set(value) {
            if (field == value) {
                return
            }
            field = value
            val array = ByteBuffer.allocate(Short.SIZE_BYTES).putShort(value).array()
            source[3] = array[0]
            source[4] = array[1]
            return
        }
    var recordType: RecordType = RecordType.UNKNOWN
        set(value) {
            field = value
            // 后三位置0
            source[2] = (source[2].toInt() and 0b11111000).toByte()
            source[2] = (source[2].toInt() or recordType.value).toByte()
        }


    private fun refreshProperties() {
        val deleteMask = 1 shl 5
        delete = source[0].toInt() and deleteMask == deleteMask
        val minRecMask = 1 shl 4
        minRec = source[0].toInt() and minRecMask == minRecMask
        val nOwnedBase = 0x0F
        nOwned = source[0].toInt() and nOwnedBase

        ByteBuffer.wrap(byteArrayOf(source[1], source[2])).let {
            val s = it.getShort()
            heapNo = (s.toInt() shr 3).toUInt()
            recordType = RecordType.entries[s.toInt() and 0b00000111]
        }
        nextRecordOffset = ByteBuffer.wrap(byteArrayOf(source[3], source[4])).getShort()
    }


    override fun toBytes(): ByteArray {
        return source
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val that = other as RecordHeader
        return source.contentEquals(that.source)
    }

    override fun hashCode(): Int {
        return source.contentHashCode()
    }

    override fun length(): Int {
        return source.size
    }

    companion object {

        fun create(type: RecordType) = RecordHeader().apply {
            when (type) {
                RecordType.PAGE -> indexHeader(this)
                RecordType.INFIMUM -> infimumHeader(this)
                RecordType.SUPREMUM -> supremumHeader(this)
                RecordType.NORMAL -> normalHeader(this)
                else -> unknownHeader(this)
            }
            refreshProperties()
        }

        fun wrap(source: ByteArray) = RecordHeader().apply {
            ConstantSize.RECORD_HEADER.checkSize(source)
            source.copyInto(this.source)
            refreshProperties()
        }

        fun copy(source: RecordHeader) = wrap(source.toBytes())


        private fun unknownHeader(recordHeader: RecordHeader) {
            recordHeader.apply {
                recordType = RecordType.UNKNOWN
                heapNo = 2U
                delete = false
                nOwned = 0
                nextRecordOffset = 0
            }
        }

        private fun normalHeader(recordHeader: RecordHeader) {
            recordHeader.apply {
                recordType = RecordType.NORMAL
                heapNo = 2U
                delete = false
                nOwned = 0
                nextRecordOffset = 0
            }
        }

        private fun indexHeader(recordHeader: RecordHeader) {
            recordHeader.apply {
                recordType = RecordType.PAGE
                heapNo = 1U
                delete = false
                nOwned = 0
                nextRecordOffset = 0
            }
        }

        private fun infimumHeader(recordHeader: RecordHeader) {
            recordHeader.apply {
                recordType = RecordType.INFIMUM
                heapNo = 0U
                delete = false
                nOwned = 1
                nextRecordOffset = ConstantSize.INFIMUM.size().toShort()
            }
        }

        private fun supremumHeader(recordHeader: RecordHeader) {
            recordHeader.apply {
                recordType = RecordType.SUPREMUM
                heapNo = 1U
                delete = false
                nOwned = 1
                nextRecordOffset = 0
            }
        }
    }
}
