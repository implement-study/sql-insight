package tech.insight.engine.innodb.page.type

import tech.insight.core.bean.Column
import tech.insight.core.bean.ReadRow
import tech.insight.core.bean.Table
import tech.insight.core.bean.value.Value
import tech.insight.core.bean.value.ValueInt
import tech.insight.engine.innodb.page.ConstantSize
import tech.insight.engine.innodb.page.IndexNode
import tech.insight.engine.innodb.page.InnoDbPage
import tech.insight.engine.innodb.page.InnodbUserRecord
import tech.insight.engine.innodb.page.compact.*
import tech.insight.engine.innodb.page.compact.RowFormatFactory.readRecordHeader
import tech.insight.engine.innodb.utils.RowComparator
import tech.insight.engine.innodb.utils.ValueNegotiator
import java.nio.ByteBuffer
import java.util.*


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class DataPage(override val page: InnoDbPage) : PageType {

    override val value: Short = FIL_PAGE_INDEX_VALUE


    /**
     * This page should be returned whether it is a root page or a downward call from the parent index page
     */
    override fun locatePage(userRecord: InnodbUserRecord): InnoDbPage {
        return page
    }

    /**
     * read page source solve a user record.
     * the offset is offset in page.
     * offset is after record header .in other words offset - record header size  means record header offset
     */
    override fun convertUserRecord(offsetInPage: Int): InnodbUserRecord {
        if (ConstantSize.INFIMUM.offset() == offsetInPage) {
            return page.infimum
        }
        if (ConstantSize.SUPREMUM.offset() == offsetInPage) {
            return page.supremum
        }
        val compact = Compact()
        compact.offsetInPage = (offsetInPage)
        compact.recordHeader = (readRecordHeader(page, offsetInPage))
        val table = page.ext.belongIndex.belongTo()
        fillNullAndVar(page, offsetInPage, compact, table)
        val variableLength: Int = compact.variables.variableLength()
        val fixLength = compactFixLength(compact, table)
        val body: ByteArray =
            Arrays.copyOfRange(page.toBytes(), offsetInPage, offsetInPage + variableLength + fixLength)
        compact.body = (body)
        compact.sourceRow = (compactReadRow(compact, table))
        return compact
    }

    override fun pageIndex(): IndexRecord {
        val firstData = page.getUserRecordByOffset(page.infimum.offset() + page.infimum.nextRecordOffset())
        val columns = page.ext.belongIndex.columns()
        val values = columns.map { it.name }.map { firstData.getValueByColumnName(it) }.toTypedArray()
        return IndexRecord(IndexNode(values, page.fileHeader.offset), page.ext.belongIndex)
    }

    override fun compare(o1: InnodbUserRecord, o2: InnodbUserRecord): Int {
        return RowComparator.primaryKeyComparator().compare(o1, o2)
    }


    /**
     * fill compact field null list and variables
     * depend on table info.
     */
    private fun fillNullAndVar(page: InnoDbPage, offset: Int, compact: Compact, table: Table) {
        var varOffset = offset
        val nullLength: Int = CompactNullList.calcNullListLength(table.ext.nullableColCount)
        varOffset -= ConstantSize.RECORD_HEADER.size() + nullLength
        val pageArr: ByteArray = page.toBytes()
        val nullListByte = Arrays.copyOfRange(pageArr, varOffset, varOffset + nullLength)
        //   read null list
        val compactNullList = CompactNullList.wrap(nullListByte)
        compact.nullList = (compactNullList)
        //   read variable
        val variableCount = variableColumnCount(table, compactNullList)
        varOffset -= variableCount
        val variableArray = Arrays.copyOfRange(pageArr, varOffset, varOffset + variableCount)
        compact.variables = (Variables(variableArray))
    }


    private fun variableColumnCount(table: Table, nullList: CompactNullList): Int {
        val columnList: List<Column> = table.columnList
        var result = 0
        for (column in columnList) {
            if (!column.notNull && nullList.isNull(column.nullListIndex)) {
                continue
            }
            if (column.variable) {
                result++
            }
        }
        return result
    }

    private fun compactFixLength(compact: Compact, table: Table): Int {
        var fixLength = 0
        for (column in table.columnList) {
            if (column.variable) {
                continue
            }
            if (column.notNull || !compact.nullList.isNull(column.nullListIndex)) {
                fixLength += column.length
            }
        }
        return fixLength
    }

    private fun compactReadRow(compact: Compact, table: Table): ReadRow {
        val columnList: List<Column> = table.columnList
        val valueList: MutableList<Value<*>> = ArrayList<Value<*>>(columnList.size)
        val bodyBuffer = ByteBuffer.wrap(compact.body)
        val iterator: Iterator<Byte> = compact.variables.iterator()
        var rowId = -1
        for (column in columnList) {
            if (!column.notNull && compact.nullList.isNull(column.nullListIndex)) {
                valueList.add(column.defaultValue)
                continue
            }
            val length = if (column.variable) iterator.next().toInt() else column.length
            val item = ByteArray(length)
            bodyBuffer[item]
            val value: Value<*> = ValueNegotiator.wrapValue(column, item)
            valueList.add(value)
            if (column.primaryKey) {
                rowId = (value as ValueInt).source
            }
        }
        val row = ReadRow(valueList, rowId.toLong())
        row.table = table
        return row
    }

    companion object {
        const val FIL_PAGE_INDEX_VALUE = 0X45bf.toShort()

    }

}
