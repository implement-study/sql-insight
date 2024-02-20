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
import tech.insight.core.bean.Column
import tech.insight.core.bean.InsertRow
import tech.insight.core.bean.ReadRow
import tech.insight.core.bean.Table
import tech.insight.core.bean.value.Value
import tech.insight.core.bean.value.ValueInt
import tech.insight.core.bean.value.ValueNull
import tech.insight.core.exception.SqlInsightException
import tech.insight.engine.innodb.page.ConstantSize
import tech.insight.engine.innodb.page.InnoDbPage
import tech.insight.engine.innodb.page.InnodbUserRecord
import tech.insight.engine.innodb.utils.ValueNegotiator
import java.nio.ByteBuffer
import java.util.*
import kotlin.math.pow

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
        compact.nullList = CompactNullList(row.belongTo())
        compact.sourceRow = row
        compact.recordHeader = RecordHeader()
        val bodyBuffer: DynamicByteBuffer = DynamicByteBuffer.allocate()
        for (insertItem in row) {
            val column: Column = insertItem.column
            val value: Value<*> = insertItem.value
            if (value is ValueNull && column.notNull) {
                throw SqlInsightException("${column.name} must not null")
            }
            if (value is ValueNull) {
                compact.nullList.setNull(column.nullListIndex)
                continue
            }
            if (column.variable) {
                val length: Int = value.length
                if (length >= 2.0.pow(Byte.SIZE_BITS)) {
                    throw SqlInsightException("length too long ")
                }
                compact.variables.addVariableLength(value.length.toByte())
            }
            bodyBuffer.append(value.toBytes())
        }
        compact.body = (bodyBuffer.toBytes())
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
        compact.offsetInPage = (offsetInPage)
        compact.recordHeader = (readRecordHeader(page, offsetInPage))
        fillNullAndVar(page, offsetInPage, compact, table)
        val variableLength: Int = compact.variables.variableLength()
        val fixLength = compactFixLength(compact, table)
        val body: ByteArray =
            Arrays.copyOfRange(page.toBytes(), offsetInPage, offsetInPage + variableLength + fixLength)
        compact.body = (body)
        compact.sourceRow = (compactReadRow(compact, table))
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
        val nullLength: Int = table.ext.nullableColCount / Byte.SIZE_BITS
        offset -= ConstantSize.RECORD_HEADER.size() - nullLength
        val pageArr: ByteArray = page.toBytes()
        val nullListByte = Arrays.copyOfRange(pageArr, offset, offset + nullLength)
        //   read null list
        val compactNullList = CompactNullList(nullListByte)
        compact.nullList = (compactNullList)
        //   read variable
        val variableCount = variableColumnCount(table, compactNullList)
        offset -= variableCount
        val variableArray = Arrays.copyOfRange(pageArr, offset, offset + variableCount)
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
}
