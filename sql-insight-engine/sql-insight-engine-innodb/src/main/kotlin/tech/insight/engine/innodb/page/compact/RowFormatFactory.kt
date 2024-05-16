/*
 * Copyright 2023 sql-insight  and the original author or authors <gongxuanzhangmelt@gmail.com>.
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
import tech.insight.core.bean.value.Value
import tech.insight.core.bean.value.ValueNull
import tech.insight.core.exception.SqlInsightException
import tech.insight.engine.innodb.page.ConstantSize
import tech.insight.engine.innodb.page.InnoDbPage
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
        compact.nullList = CompactNullList.allocate(row.belongTo())
        compact.sourceRow = row
        compact.recordHeader = RecordHeader.create(RecordType.NORMAL)
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
                if (length > UByte.MAX_VALUE.toInt()) {
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
     * @param page   innodb page
     * @param offset record offset, the record header offset = offset - record header size
     * @return record
     */
    fun readRecordHeader(page: InnoDbPage, offset: Int): RecordHeader {
        val recordHeaderSize: Int = ConstantSize.RECORD_HEADER.size()
        val headerArr: ByteArray = Arrays.copyOfRange(page.toBytes(), offset - recordHeaderSize, offset)
        return RecordHeader.wrap(headerArr)
    }


}
