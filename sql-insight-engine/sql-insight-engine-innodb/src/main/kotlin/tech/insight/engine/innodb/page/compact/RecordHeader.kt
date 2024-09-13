package tech.insight.engine.innodb.page.compact

import io.netty.buffer.ByteBuf
import tech.insight.buffer.SerializableObject
import tech.insight.buffer.copyBuf
import tech.insight.buffer.coverBits
import tech.insight.buffer.getAllBytes
import tech.insight.buffer.isOne
import tech.insight.buffer.setOne
import tech.insight.buffer.setZero
import tech.insight.buffer.subByte
import tech.insight.buffer.subShort
import tech.insight.core.annotation.Unused
import tech.insight.engine.innodb.core.Lengthable
import tech.insight.engine.innodb.page.ConstantSize

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
 * @author gongxuanzhangmelt@gmail.com
 */
class RecordHeader(private val source: ByteBuf) : SerializableObject, Lengthable {

    init {
        require(source.readableBytes() == ConstantSize.RECORD_HEADER.size) {
            "source size must be ${ConstantSize.RECORD_HEADER.size}"
        }
    }

    var deleteMask: Boolean = source.getByte(0).isOne(5)
        set(value) {
            if (field == value) {
                return
            }
            field = value
            if (value) {
                source.setByte(0, source.getByte(0).setOne(5).toInt())
            } else {
                source.setByte(0, source.getByte(0).setZero(5).toInt())
            }
        }

    @Unused
    var minRec: Boolean = source.getByte(0).isOne(4)
        set(value) {
            if (field == value) {
                return
            }
            field = value
            if (value) {
                source.setByte(0, source.getByte(0).setOne(4).toInt())
            } else {
                source.setByte(0, source.getByte(0).setZero(4).toInt())
            }
        }

    var nOwned: Int = source.getByte(0).subByte(4)
        set(value) {
            if (field == value) {
                return
            }
            require(value in OWNED_RANGE) { "nOwned must in $OWNED_RANGE" }
            field = value
            source.setByte(0, source.getByte(0).coverBits(value, 4).toInt())
        }

    var heapNo: Int = source.getShort(1).subShort(3, 16)
        set(value) {
            if (field == value) {
                return
            }
            require(value in HEAP_NO_RANGE) { "heapNo must in $HEAP_NO_RANGE" }
            field = value
            source.setShort(1, source.getShort(1).coverBits(value, 3, 16).toInt())
        }

    var recordType: RecordType = RecordType.valueOf(source.getByte(2).subByte(3))
        set(newType) {
            if (newType == field) {
                return
            }
            field = newType
            source.setByte(2, source.getByte(2).coverBits(newType.value, 3).toInt())
        }

    var nextRecordOffset: Int = source.getShort(3).toInt()
        set(value) {
            if (field == value) {
                return
            }
            require(value in NEXT_RECORD_RANGE) { "nextRecordOffset must in $NEXT_RECORD_RANGE" }
            field = value
            source.setShort(3, value)
        }

    override fun toBytes(): ByteArray {
        return source.getAllBytes()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val that = other as RecordHeader
        return heapNo == that.heapNo && nextRecordOffset == that.nextRecordOffset
    }

    override fun hashCode(): Int {
        return source.hashCode()
    }


    override fun length(): Int {
        return ConstantSize.RECORD_HEADER.size
    }

    companion object {

        private val HEAP_NO_RANGE = IntRange(0, (1 shl 13) - 1)
        private val OWNED_RANGE = IntRange(0, (1 shl 4) - 1)
        private val NEXT_RECORD_RANGE = IntRange(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())

        fun create(type: RecordType) = RecordHeader(copyBuf(type.arraySupplier()))

        fun copy(source: RecordHeader) = RecordHeader(copyBuf(source.toBytes()))

    }
}
