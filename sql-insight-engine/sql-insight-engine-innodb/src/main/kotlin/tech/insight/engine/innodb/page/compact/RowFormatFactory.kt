/*
 * Copyright 2023 java-mysql  and the original author or authors <gongxuanzhangmelt@gmail.com>.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/implement-study/sql-insight/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.insight.engine.innodb.page.compact

import org.gongxuanzhang.easybyte.core.DynamicByteBuffer
import tech.insight.engine.innodb.page.InnoDbPage
import tech.insight.engine.innodb.page.InnodbUserRecord
import java.nio.ByteBuffer
import java.util.*

/**
 * @author gongxuanzhangmelt@gmail.com
 */
object RowFormatFactory {
    /**
     * create row format from insert row.
     *
     * @return the compact don't have record header
     */
    fun compactFromInsertRow(row: InsertRow): Compact {
        val compact = Compact()
        compact.variables = Variables()
        compact.nullList = CompactNullList(row.getTable())
        compact.sourceRow = row
        compact.setRecordHeader(RecordHeader())
        val bodyBuffer: DynamicByteBuffer = DynamicByteBuffer.allocate()
        for (insertItem in row) {
            val column: Column = insertItem.getColumn()
            val value: Value = insertItem.getValue()
            if (value.getSource() == null && column.isNotNull()) {
                throw SqlInsightException(column.getName() + " must not null")
            }
            if (value.getSource() == null) {
                compact.nullList!!.setNull(column.getNullListIndex())
                continue
            }
            if (column.isVariable()) {
                val length: Int = value.getLength()
                if (length >= 2.pow(java.lang.Byte.SIZE.toDouble())) {
                    throw SqlInsightException("length too long ")
                }
                compact.variables!!.addVariableLength(value.getLength() as Byte)
            }
            bodyBuffer.append(value.toBytes())
        }
        compact.setBody(bodyBuffer.toBytes())
        return compact
    }

    /**
     * read page source solve a user record.
     * the offset is offset in page.
     * offset is after record header .in other words offset - record header size  means record header offset
     */
    fun readRecordInPage(page: InnoDbPage, offsetInPage: Int, table: Table): InnodbUserRecord {
        if (ConstantSize.INFIMUM.offset() == offsetInPage) {
            return page.infimum
        }
        if (ConstantSize.SUPREMUM.offset() == offsetInPage) {
            return page.supremum
        }
        val compact = Compact()
        compact.setOffsetInPage(offsetInPage)
        compact.setRecordHeader(readRecordHeader(page, offsetInPage))
        fillNullAndVar(page, offsetInPage, compact, table)
        val variableLength: Int = compact.getVariables().variableLength()
        val fixLength = compactFixLength(compact, table)
        val body: ByteArray =
            Arrays.copyOfRange(page.toBytes(), offsetInPage, offsetInPage + variableLength + fixLength)
        compact.setBody(body)
        compact.setSourceRow(compactReadRow(compact, table))
        return compact
    }

    /**
     * @param page   innodb page
     * @param offset record offset, the record header offset = offset - record header size
     * @return record
     */
    fun readRecordHeader(page: InnoDbPage, offset: Int): RecordHeader {
        val recordHeaderSize: Int = ConstantSize.RECORD_HEADER.size()
        val headerArr: ByteArray = Arrays.copyOfRange(page.toBytes(), offset - recordHeaderSize, offset)
        return RecordHeader(headerArr)
    }

    /**
     * fill compact field null list and variables
     * depend on table info.
     */
    private fun fillNullAndVar(page: InnoDbPage, offset: Int, compact: Compact, table: Table) {
        var offset = offset
        val nullLength: Int = table.getExt().getNullableColCount() / java.lang.Byte.SIZE
        offset -= ConstantSize.RECORD_HEADER.size() - nullLength
        val pageArr: ByteArray = page.toBytes()
        val nullListByte = Arrays.copyOfRange(pageArr, offset, offset + nullLength)
        //   read null list
        val compactNullList = CompactNullList(nullListByte)
        compact.setNullList(compactNullList)
        //   read variable
        val variableCount = variableColumnCount(table, compactNullList)
        offset -= variableCount
        val variableArray = Arrays.copyOfRange(pageArr, offset, offset + variableCount)
        compact.setVariables(Variables(variableArray))
    }

    private fun variableColumnCount(table: Table, nullList: CompactNullList): Int {
        val columnList: List<Column> = table.getColumnList()
        var result = 0
        for (column in columnList) {
            if (!column.isNotNull() && nullList.isNull(column.getNullListIndex())) {
                continue
            }
            if (column.isVariable()) {
                result++
            }
        }
        return result
    }

    private fun compactFixLength(compact: Compact, table: Table): Int {
        var fixLength = 0
        for (column in table.getColumnList()) {
            if (column.isVariable()) {
                continue
            }
            if (column.isNotNull() || !compact.getNullList().isNull(column.getNullListIndex())) {
                fixLength += column.getDataType().getLength()
            }
        }
        return fixLength
    }

    private fun compactReadRow(compact: Compact, table: Table): ReadRow {
        val columnList: List<Column> = table.getColumnList()
        val valueList: MutableList<Value> = ArrayList<Value>(columnList.size)
        val bodyBuffer = ByteBuffer.wrap(compact.getBody())
        val iterator: Iterator<Byte> = compact.getVariables().iterator()
        var rowId = -1
        for (column in columnList) {
            if (!column.isNotNull() && compact.getNullList().isNull(column.getNullListIndex())) {
                valueList.add(if (column.getDefaultValue() == null) ValueNull.getInstance() else column.getDefaultValue())
                continue
            }
            val length = if (column.isVariable()) iterator.next().toInt() else column.getDataType().getLength()
            val item = ByteArray(length)
            bodyBuffer[item]
            val value: Value = ValueNegotiator.wrapValue(column, item)
            valueList.add(value)
            if (column.isPrimaryKey()) {
                rowId = (value as ValueInt).getSource()
            }
        }
        val row = ReadRow(valueList, rowId)
        row.setTable(table)
        return row
    }
}
