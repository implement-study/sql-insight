package tech.insight.engine.innodb.page.compact

import org.gongxuanzhang.easybyte.core.ByteWrapper
import tech.insight.core.extension.setBit0
import tech.insight.core.extension.setBit1
import tech.insight.engine.innodb.page.ConstantSize
import tech.insight.engine.innodb.page.PageObject
import java.nio.ByteBuffer

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
class RecordHeader : ByteWrapper, PageObject {
    private val source: ByteArray
    var delete = false
    private var minRec = false
    var nOwned = 0
    private var heapNo = 0
    var nextRecordOffset = 0
    lateinit var recordType: RecordType

    constructor() {
        source = ByteArray(5)
    }

    constructor(source: ByteArray) {
        ConstantSize.RECORD_HEADER.checkSize(source)
        this.source = source
        swapProperties()
    }

    private fun initType() {
        val typeValue = source[2].toInt() and 0x07
        for (type in RecordType.entries) {
            if (type.value == typeValue) {
                recordType = type
                return
            }
        }
        throw IllegalArgumentException()
    }

    private fun swapProperties() {
        val deleteMask = 1 shl 5
        delete = source[0].toInt() and deleteMask == deleteMask
        val minRecMask = 1 shl 4
        minRec = source[0].toInt() and minRecMask == minRecMask
        val nOwnedBase = 0x0F
        nOwned = source[0].toInt() and nOwnedBase
        val high = java.lang.Byte.toUnsignedInt(source[1])
        val low = java.lang.Byte.toUnsignedInt(source[2])
        heapNo = high shl 8 or low shr 3
        nextRecordOffset = (source[3].toInt() and 0xFF shl 8 or (source[4].toInt() and 0xFF)).toShort().toInt()
        initType()
    }

    fun setDelete(delete: Boolean): RecordHeader {
        if (this.delete == delete) {
            return this
        }
        this.delete = delete
        if (delete) {
            source[0] = source[0].setBit1(5)
        } else {
            source[0] = source[0].setBit0(5)
        }
        return this
    }

    fun setMinRec(minRec: Boolean): RecordHeader {
        if (this.minRec == minRec) {
            return this
        }
        this.minRec = minRec
        if (minRec) {
            source[0] = source[0].setBit1(4)
        } else {
            source[0] = source[0].setBit0(5)
        }
        return this
    }

    fun setNOwned(nOwned: Int): RecordHeader {
        if (this.nOwned == nOwned) {
            return this
        }
        //  清零source[0]的后四位
        source[0] = (source[0].toInt() and 0xF0.toByte().toInt()).toByte()
        source[0] = (source[0].toInt() or nOwned.toByte().toInt()).toByte()
        this.nOwned = nOwned
        return this
    }

    fun setHeapNo(heapNo: Int): RecordHeader {
        if (this.heapNo == heapNo) {
            return this
        }
        this.heapNo = heapNo
        source[1] = (heapNo shr 5).toByte()
        source[2] = (source[2].toInt() and 7).toByte()
        source[2] = (source[2].toInt() or (heapNo shl 3).toByte().toInt()).toByte()
        return this
    }

    fun setNextRecordOffset(nextRecordOffset: Int): RecordHeader {
        if (this.nextRecordOffset == nextRecordOffset) {
            return this
        }
        this.nextRecordOffset = nextRecordOffset
        val array = ByteBuffer.allocate(java.lang.Short.BYTES).putShort(nextRecordOffset.toShort()).array()
        source[3] = array[0]
        source[4] = array[1]
        return this
    }

    fun setRecordType(recordType: RecordType): RecordHeader {
        if (this.recordType == recordType) {
            return this
        }
        this.recordType = recordType
        // 后三位置0
        source[2] = (source[2].toInt() and 248.toByte().toInt()).toByte()
        source[2] = (source[2].toInt() or recordType.value.toByte().toInt()).toByte()
        return this
    }

    override fun toBytes(): ByteArray {
        return source
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }
        val that = o as RecordHeader
        return source.contentEquals(that.source)
    }

    override fun hashCode(): Int {
        return source.contentHashCode()
    }

    override fun length(): Int {
        return source.size
    }
}
