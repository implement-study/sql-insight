package tech.insight.engine.innodb.page.compact

import org.gongxuanzhang.easybyte.core.DynamicByteBuffer
import tech.insight.core.annotation.Unused
import tech.insight.core.bean.Row
import tech.insight.core.bean.Table
import tech.insight.core.bean.value.Value
import tech.insight.engine.innodb.page.ConstantSize
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

    override fun offset(): Int {
        require(offsetInPage != -1) { "unknown offset" }
        return offsetInPage
    }

    override fun setOffset(offset: Int) {
        offsetInPage = offset
    }

    override fun nextRecordOffset(): Int {
        return recordHeader.nextRecordOffset
    }

    override fun deleteSign(): Boolean {
        return recordHeader.delete
    }

    override fun toString(): String {
        return "Compact{" +
                "sourceRow=" + sourceRow +
                '}'
    }
}
