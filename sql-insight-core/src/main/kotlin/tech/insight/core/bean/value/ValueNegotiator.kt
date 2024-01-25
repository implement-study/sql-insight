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
package tech.insight.core.bean.value

import org.gongxuanzhang.sql.insight.core.exception.DataTooLongException
import java.util.function.Consumer

/**
 * support [ValueVisitor] any way
 *
 * @author gongxuanzhangmelt@gmail.com
 */
object ValueNegotiator {
    /**
     * column default value
     */
    fun columnDefaultValue(column: Column): Consumer<Value> {
        return Consumer<Value> { value: Value ->
            if (value.getLength() > column.getDataType().getLength()) {
                throw DataTooLongException(column)
            }
            column.setDefaultValue(value)
        }
    }

    fun wrapValue(column: Column, value: ByteArray?): Value {
        val dataType: DataType = column.getDataType()
        return when (dataType.getType()) {
            INT -> ValueInt(BitUtils.byteArrayToInt(value))
            VARCHAR, CHAR -> ValueVarchar(String(value!!))
            else -> throw IllegalArgumentException()
        }
    }
}
