package tech.insight.engine.innodb.page.compact

import tech.insight.buffer.SerializableObject
import tech.insight.buffer.byteArray
import tech.insight.buffer.compose
import tech.insight.buffer.coverBits
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
class RecordHeader(private val source: ByteArray) : SerializableObject, Lengthable {

    init {
        require(source.size == ConstantSize.RECORD_HEADER.size) {
            "source size must be ${ConstantSize.RECORD_HEADER.size}"
        }
    }

    var deleteMask: Boolean = source[0].isOne(5)
        set(value) {
            if (field == value) {
                return
            }
            field = value
            if (value) {
                source[0] = source[0].setOne(5)
            } else {
                source[0] = source[0].setZero(5)
            }
        }

    @Unused
    var minRec: Boolean = source[0].isOne(4)
        set(value) {
            if (field == value) {
                return
            }
            field = value
            if (value) {
                source[0] = source[0].setOne(4)
            } else {
                source[0] = source[0].setZero(4)
            }
        }

    var nOwned: Int = source[0].subByte(4)
        set(value) {
            if (field == value) {
                return
            }
            require(value in OWNED_RANGE) { "nOwned must in $OWNED_RANGE" }
            field = value
            source[0] = source[0].coverBits(value, 4)
        }

    var heapNo: Int = compose(source[1], source[2]).subShort(3, 16)
        set(value) {
            if (field == value) {
                return
            }
            require(value in HEAP_NO_RANGE) { "heapNo must in $HEAP_NO_RANGE" }
            field = value
            val newByteArray = compose(source[1], source[2]).coverBits(value, 3, 16).byteArray()
            source[1] = newByteArray[0]
            source[2] = newByteArray[1]
        }

    var recordType: RecordType = RecordType.UNKNOWN
        set(value) {
            if (value == field) {
                return
            }
            field = value
            source[2] = source[2].coverBits(value.value, 3)
        }

    var nextRecordOffset: Int = compose(source[3], source[4]).toInt()
        set(value) {
            if (field == value) {
                return
            }
            require(value in NEXT_RECORD_RANGE) { "nextRecordOffset must in $NEXT_RECORD_RANGE" }
            field = value
            val newByteArray = value.toShort().byteArray()
            source[3] = newByteArray[0]
            source[4] = newByteArray[1]
            return
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

        private val HEAP_NO_RANGE = IntRange(0, (1 shl 13) - 1)
        private val OWNED_RANGE = IntRange(0, (1 shl 4) - 1)
        private val NEXT_RECORD_RANGE = IntRange(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())

        fun create(type: RecordType) = RecordHeader(type.arraySupplier())

        fun wrap(source: ByteArray) = RecordHeader(source).apply {
            ConstantSize.RECORD_HEADER.checkSize(source)
        }

        fun copy(source: RecordHeader) = RecordHeader(source.toBytes().copyOf())

    }
}
