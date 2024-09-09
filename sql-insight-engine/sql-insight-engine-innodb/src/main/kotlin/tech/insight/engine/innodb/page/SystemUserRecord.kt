package tech.insight.engine.innodb.page

import io.netty.buffer.ByteBuf
import java.nio.charset.Charset
import tech.insight.buffer.byteBuf
import tech.insight.buffer.getAllBytes
import tech.insight.core.bean.Row
import tech.insight.core.bean.Table
import tech.insight.core.bean.value.Value
import tech.insight.engine.innodb.index.InnodbIndex
import tech.insight.engine.innodb.page.compact.RecordHeader


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

    override fun remove() {
        throw UnsupportedOperationException("infimum and supremum not support remove!")
    }

    private fun systemUserRecordUnsupported(): Nothing {
        throw UnsupportedOperationException("infimum and supremum not support operator!")
    }
}

class Supremum(override val belongPage: InnoDbPage) : SystemUserRecord {

    val source: ByteBuf = belongPage.source.slice(ConstantSize.SUPREMUM.offset, ConstantSize.SUPREMUM.size)

    /**
     * 5 bytes
     */
    override val recordHeader = RecordHeader(source.slice(0, ConstantSize.RECORD_HEADER.size).getAllBytes())

    /**
     * 8 bytes as "supremum"
     */
    private val body: ByteBuf = source.slice(ConstantSize.RECORD_HEADER.size, ConstantSize.SUPREMUM_BODY.size)

    init {
        val expect = body.getCharSequence(0, ConstantSize.SUPREMUM_BODY.size, Charset.defaultCharset())
        require(SUPREMUM_BODY == expect) { "supremum body must be $SUPREMUM_BODY" }
    }

    override fun rowBytes(): ByteArray {
        return source.array()
    }

    override fun absoluteOffset(): Int {
        return ConstantSize.SUPREMUM.offset + ConstantSize.RECORD_HEADER.size
    }

    /**
     * supremum next is infimum
     */
    override fun nextRecordOffset(): Int {
        return Infimum.OFFSET_IN_PAGE - OFFSET_IN_PAGE
    }

    override fun nextRecord(): InnodbUserRecord {
        return this.belongPage.infimum
    }

    override fun preRecord(): InnodbUserRecord {
        var candidate = belongPage.pageDirectory.slots.last().smaller().maxRecord()
        while (candidate.nextRecordOffset() + candidate.absoluteOffset() != this.absoluteOffset()) {
            candidate = candidate.nextRecord()
        }
        return candidate
    }

    override fun beforeSplitOffset(): Int {
        return recordHeader.length()
    }

    override fun belongIndex(): InnodbIndex {
        return belongPage.ext.belongIndex
    }

    override fun indexNode(): InnodbUserRecord {
        throw UnsupportedOperationException("this is supremum!")
    }

    override fun groupMax(): InnodbUserRecord {
        return this
    }


    override fun length(): Int {
        return ConstantSize.SUPREMUM.size
    }

    override fun toString(): String {
        return "[body:$SUPREMUM_BODY]"
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
        return source == other.source
    }

    override fun hashCode(): Int {
        return source.hashCode()
    }

    companion object {

        const val SUPREMUM_BODY = "supremum"

        val OFFSET_IN_PAGE = ConstantSize.SUPREMUM_BODY.offset

        val SUPREMUM_BODY_ARRAY: ByteArray = SUPREMUM_BODY.toByteArray()

        fun wrap(bytes: ByteArray, belongToPage: InnoDbPage) = Supremum(belongToPage)
    }

}

/**
 * min record in group
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 */
class Infimum(override val belongPage: InnoDbPage) : SystemUserRecord {

    val source: ByteBuf = belongPage.source.slice(ConstantSize.INFIMUM.offset, ConstantSize.INFIMUM.size)

    /**
     * 5 bytes.
     */
    override val recordHeader = RecordHeader(source.slice(0, ConstantSize.RECORD_HEADER.size).getAllBytes())

    /**
     * fixed 8 bytes. "infimum" is 7 bytes . fill 0 zero occupy the space
     */
    private val body: ByteBuf = source.slice(ConstantSize.RECORD_HEADER.size, ConstantSize.INFIMUM_BODY.size)

    init {
        require(INFIMUM_BODY_ARRAY.contentEquals(body.getAllBytes())) { "infimum body must be $INFIMUM_BODY" }
    }

    override fun rowBytes(): ByteArray {
        return source.array()
    }

    override fun beforeSplitOffset(): Int {
        return recordHeader.length()
    }

    override fun belongIndex(): InnodbIndex {
        return belongPage.ext.belongIndex
    }

    override fun indexNode(): InnodbUserRecord {
        throw UnsupportedOperationException("this is infimum!")
    }

    override fun groupMax(): InnodbUserRecord {
        return this
    }

    override fun nextRecordOffset(): Int {
        return recordHeader.nextRecordOffset
    }

    override fun nextRecord(): InnodbUserRecord {
        return this.belongPage.getUserRecordByOffset(this.absoluteOffset() + nextRecordOffset())
    }

    override fun preRecord(): InnodbUserRecord {
        throw UnsupportedOperationException("infimum dont have pre record")
    }


    override fun absoluteOffset(): Int {
        return ConstantSize.INFIMUM.offset + ConstantSize.RECORD_HEADER.size
    }


    override fun length(): Int {
        return ConstantSize.INFIMUM.size + ConstantSize.RECORD_HEADER.size
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
        return source == other.source
    }


    override fun hashCode(): Int {
        return source.hashCode()
    }

    override fun toString(): String {
        return "[body:$INFIMUM_BODY]"
    }

    companion object {

        const val INFIMUM_BODY = "infimum"

        val OFFSET_IN_PAGE = ConstantSize.INFIMUM_BODY.offset

        val INFIMUM_BODY_ARRAY: ByteArray =
            byteBuf(ConstantSize.SUPREMUM_BODY.size).writeBytes(INFIMUM_BODY.toByteArray()).writeByte(0).array()
    }
}

