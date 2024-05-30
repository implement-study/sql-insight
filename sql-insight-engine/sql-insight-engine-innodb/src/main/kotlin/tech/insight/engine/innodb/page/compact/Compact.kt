package tech.insight.engine.innodb.page.compact

import java.util.*
import org.gongxuanzhang.easybyte.core.DynamicByteBuffer
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
class Compact : InnodbUserRecord {
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

    lateinit var sourceRow: Row

    var offsetInPage = -1

    lateinit var belongIndex: InnodbIndex

    override lateinit var belongPage: InnoDbPage

    override fun rowBytes(): ByteArray {
        val buffer: DynamicByteBuffer = DynamicByteBuffer.allocate()
        buffer.append(variables.toBytes())
        buffer.append(nullList.toBytes())
        buffer.append(recordHeader.toBytes())
        buffer.append(body)
        return buffer.toBytes()
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
        //    record header must write "ConstantSize.RECORD_HEADER.size()"
        //    because  the compact may come from insert row result in NullPointException
        return variables.length() + nullList.length() + ConstantSize.RECORD_HEADER.size() + body.size
    }

    override fun beforeSplitOffset(): Int {
        return variables.length() + nullList.length() + ConstantSize.RECORD_HEADER.size()
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


    override fun absoluteOffset(): Int {
        require(offsetInPage != -1) { "unknown offset" }
        return offsetInPage
    }

    override fun setAbsoluteOffset(offset: Int) {
        offsetInPage = offset
    }

    override fun nextRecordOffset(): Int {
        return recordHeader.nextRecordOffset.toInt()
    }

    override fun deleteSign(): Boolean {
        return recordHeader.delete
    }

    override fun toString(): String {
        return "$sourceRow"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Compact) return false

        return (variables != other.variables ||
                nullList != other.nullList ||
                recordHeader != other.recordHeader ||
                !body.contentEquals(other.body) ||
                rowId != other.rowId ||
                transactionId != other.transactionId ||
                rollPointer != other.rollPointer ||
                sourceRow != other.sourceRow ||
                offsetInPage != other.offsetInPage)
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
        result = 31 * result + offsetInPage
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
        val bodyBuffer = DynamicByteBuffer.allocate()
        belongIndex.columns().forEach {
            bodyBuffer.append(getValueByColumnName(it.name).toBytes())
        }
        bodyBuffer.appendInt(belongPage.fileHeader.offset)
        return bodyBuffer.toBytes()
    }

    private fun indexRow(): Row {
        val indexValue = belongIndex.columns().map { getValueByColumnName(it.name) }
        return ReadRow(indexValue, sourceRow.rowId).apply {
            this.table = sourceRow.belongTo()
        }

    }

}
