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

import tech.insight.buffer.byteBuf
import tech.insight.buffer.getAllBytes
import tech.insight.core.bean.Column
import tech.insight.core.bean.NormalRow
import tech.insight.core.bean.UpdateRow
import tech.insight.core.bean.condition.Expression
import tech.insight.core.bean.value.Value
import tech.insight.core.bean.value.ValueNull
import tech.insight.core.exception.SqlInsightException

/**
 * @author gongxuanzhangmelt@gmail.com
 */
object RowFormatFactory {
    /**
     * create row format from normal row.
     *
     * @return the compact don't have record header
     */
    fun compactFromNormalRow(row: NormalRow): Compact {
        val compact = Compact()
        compact.variables = Variables.create()
        compact.nullList = CompactNullList.allocate(row.belongTo())
        compact.sourceRow = row
        compact.recordHeader = RecordHeader.create(RecordType.NORMAL)
        val buf = byteBuf()
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
                compact.variables.appendVariableLength(value.length.toUByte())
            }
            buf.writeBytes(value.toBytes())
        }
        compact.body = buf.getAllBytes()
        return compact
    }

    fun compactFromUpdateRow(row: Compact, updateFields: Map<String, Expression>): Compact {
        val newRow = UpdateRow(row, updateFields)
        return compactFromNormalRow(newRow)
    }


}
