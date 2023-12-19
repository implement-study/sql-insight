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

package org.gongxuanzhang.sql.insight.core.object.value;

import org.gongxuanzhang.sql.insight.core.exception.DataTooLongException;
import org.gongxuanzhang.sql.insight.core.object.Column;
import org.gongxuanzhang.sql.insight.core.object.DataType;
import org.gongxuanzhang.sql.insight.core.tool.BitUtils;

import java.util.function.Consumer;

/**
 * support {@link ValueVisitor} any way
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public class ValueNegotiator {

    private ValueNegotiator() {

    }

    /**
     * column default value
     **/
    public static Consumer<Value> columnDefaultValue(Column column) {
        return value -> {
            if (value.getLength() > column.getDataType().getLength()) {
                throw new DataTooLongException(column);
            }
            column.setDefaultValue(value);
        };
    }

    public static Value wrapValue(Column column, byte[] value) {
        DataType dataType = column.getDataType();
        switch (dataType.getType()) {
            case INT:
                return new ValueInt(BitUtils.byteArrayToInt(value));
            case VARCHAR:
            case CHAR:
                return new ValueVarchar(new String(value));
            default:
                throw new IllegalArgumentException();
        }
    }

}
