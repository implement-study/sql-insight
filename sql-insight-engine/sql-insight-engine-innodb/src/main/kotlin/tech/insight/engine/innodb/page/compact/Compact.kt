package tech.insight.engine.innodb.page.compact

import java.util.*
import tech.insight.buffer.byteBuf
import tech.insight.buffer.getAllBytes
import tech.insight.core.annotation.Unused
import tech.insight.core.bean.ReadRow
import tech.insight.core.bean.Row
import tech.insight.core.bean.Table
import tech.insight.core.bean.value.Value
import tech.insight.core.bean.value.ValueNull
import tech.insight.engine.innodb.index.InnodbIndex
import tech.insight.engine.innodb.page.ConstantSize
import tech.insight.engine.innodb.page.InnoDbPage
import tech.insight.engine.innodb.page.InnodbUserRecord


/**
 * @author gongxuanzhangmelt@gmail.com
 */
open class Compact : InnodbUserRecord {
    /**
     * variable column list
     */
    lateinit var variables: Variables

    /**
     * null list.
     * size is table nullable column count / 8.
     */
    lateinit var nullList: CompactNullList

    /**
     * record header 5 bytes
     */
    override lateinit var recordHeader: RecordHeader

    /**
     * 真实记录
     */
    lateinit var body: ByteArray

    /**
     * 6字节  唯一标识
     */
    @Unused
    override var rowId: Long = 0

    /**
     * 事务id  6字节
     */
    @Unused
    var transactionId: Long = 0

    /**
     * 7字节，回滚指针
     */
    @Unused
    var rollPointer: Long = 0

    var offset: Int = -1

    lateinit var sourceRow: Row

    lateinit var belongIndex: InnodbIndex

    override lateinit var parentPage: InnoDbPage

    override fun rowBytes(): ByteArray {
        return byteBuf()
            .writeBytes(variables.toBytes())
            .writeBytes(nullList.toBytes())
            .writeBytes(recordHeader.toBytes())
            .writeBytes(body)
            .getAllBytes()
    }

    override val values: List<Value<*>>
        get() = sourceRow.values


    override fun getValueByColumnName(colName: String): Value<*> {
        return sourceRow.getValueByColumnName(colName)
    }

    override fun belongTo(): Table {
        return sourceRow.belongTo()
    }

    override fun length(): Int {
        //    record header must write "ConstantSize.RECORD_HEADER.size"
        //    because  the compact may come from insert row result in NullPointException
        return variables.length() + nullList.length() + ConstantSize.RECORD_HEADER.size + body.size
    }

    override fun beforeSplitOffset(): Int {
        return variables.length() + nullList.length() + ConstantSize.RECORD_HEADER.size
    }

    override fun indexKey(): Array<Value<*>> {
        val columns = belongIndex.columns()
        return columns.map { it.name }.map { this.getValueByColumnName(it) }.toTypedArray()
    }

    override fun belongIndex(): InnodbIndex {
        return belongIndex
    }

    override fun indexNode(): InnodbUserRecord {
        if (this.recordHeader.recordType == RecordType.PAGE) {
            return this
        }
        //  belong 
        val indexCompact = Compact()
        indexCompact.sourceRow = indexRow()
        indexCompact.recordHeader = RecordHeader.copy(recordHeader)
        indexCompact.recordHeader.recordType = RecordType.PAGE
        indexCompact.recordHeader.nOwned = 0
        indexCompact.belongIndex = belongIndex
        indexCompact.variables = indexVariables()
        indexCompact.nullList = indexNullList()
        indexCompact.body = indexBody()
        return indexCompact
    }

    override fun remove() {
        if (this.recordHeader.deleteMask) {
            return
        }
        this.recordHeader.deleteMask = true
        val preRecord = this.preRecord()
        val nextRecord = this.nextRecord()
        preRecord.recordHeader.nextRecordOffset = nextRecord.offsetInPage() - preRecord.offsetInPage()
        if (this.recordHeader.nOwned == 1) {
            parentPage.pageDirectory.requireSlotByOffset(this.offsetInPage()).remove()
        } else if (this.recordHeader.nOwned > 1) {
            preRecord.recordHeader.nOwned = this.recordHeader.nOwned - 1
            parentPage.pageDirectory.replace(this.offsetInPage(), preRecord.offsetInPage())
        }
        parentPage.pageHeader.recordCount--
        parentPage.pageHeader.garbage += this.length()
        if (parentPage.pageHeader.deleteStart != 0) {
            recordHeader.nextRecordOffset = parentPage.pageHeader.deleteStart - this.offsetInPage()
        } else {
            recordHeader.nextRecordOffset = 0
        }
        parentPage.pageHeader.deleteStart = this.offsetInPage()
        indexNode().remove()
    }

    override fun groupMax(): InnodbUserRecord {
        var candidate: InnodbUserRecord = this
        while (candidate.recordHeader.nOwned == 0) {
            candidate = candidate.nextRecord()
        }
        return candidate
    }

    /**
     * in cluster index,if compact record is index node , the point means sub page offset, otherwise is empty.
     * in second index ,if compact record is index node , the point means sub page offset, otherwise is primary key.
     */
    fun point(): ByteArray {
        if (this.recordHeader.recordType == RecordType.PAGE) {
            return Arrays.copyOfRange(body, body.size - 4, body.size)
        }
        return ByteArray(0)
    }


    override fun offsetInPage(): Int {
        require(offset != -1) { "unknown offset" }
        return offset
    }

    override fun setOffsetInPage(offset: Int) {
        this.offset = offset
    }

    override fun nextRecordOffset(): Int {
        return recordHeader.nextRecordOffset
    }

    override fun deleteSign(): Boolean {
        return recordHeader.deleteMask
    }

    override fun nextRecord(): InnodbUserRecord {
        if (this.recordHeader.deleteMask && nextRecordOffset() == 0) {
            throw NoSuchElementException("next record not found")
        }
        return this.parentPage.getUserRecordByOffset(this.offsetInPage() + nextRecordOffset())
    }

    override fun preRecord(): InnodbUserRecord {
        val groupMaxRecord = groupMax()
        val slot = parentPage.pageDirectory.requireSlotByOffset(groupMaxRecord.offsetInPage())
        var candidate = slot.minRecord()
        while (candidate.nextRecordOffset() + candidate.offsetInPage() != this.offsetInPage()) {
            candidate = candidate.nextRecord()
        }
        return candidate
    }

    override fun toString(): String {
        return "$sourceRow"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Compact) return false
        return this.parentPage === other.parentPage && this.offset == other.offset
    }

    override fun hashCode(): Int {
        var result = variables.hashCode()
        result = 31 * result + nullList.hashCode()
        result = 31 * result + recordHeader.hashCode()
        result = 31 * result + body.contentHashCode()
        result = 31 * result + rowId.hashCode()
        result = 31 * result + transactionId.hashCode()
        result = 31 * result + rollPointer.hashCode()
        result = 31 * result + sourceRow.hashCode()
        result = 31 * result + offset
        return result
    }

    /**
     * variables that the compact transfer to index node
     */
    private fun indexVariables(): Variables {
        val variables = Variables.create()
        belongIndex.columns()
            .filter { it.variable }
            .map { this.getValueByColumnName(it.name) }
            .forEach { variables.appendVariableLength(it.length.toUByte()) }
        return variables
    }

    /**
     * null list that the compact transfer to index node
     */
    private fun indexNullList(): CompactNullList {
        val indexNullList = CompactNullList.allocate(belongIndex)
        if (indexNullList.length() == 0) {
            return indexNullList
        }
        belongIndex.columns().filter { !it.notNull }.forEachIndexed { index, column ->
            if (getValueByColumnName(column.name) is ValueNull) {
                indexNullList.setNull(index)
            }
        }
        return indexNullList
    }

    private fun indexBody(): ByteArray {
        val bodyBuffer = byteBuf()
        belongIndex.columns().map {
            getValueByColumnName(it.name).toBytes()
        }.forEach {
            bodyBuffer.writeBytes(it)
        }
        bodyBuffer.writeInt(parentPage.fileHeader.offset)
        return bodyBuffer.getAllBytes()
    }

    private fun indexRow(): Row {
        val indexValue = belongIndex.columns().map { getValueByColumnName(it.name) }
        return ReadRow(indexValue, sourceRow.rowId).apply {
            this.table = sourceRow.belongTo()
        }
    }

}
