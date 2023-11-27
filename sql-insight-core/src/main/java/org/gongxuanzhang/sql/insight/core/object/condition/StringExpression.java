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

package org.gongxuanzhang.sql.insight.core.object.condition;

import org.gongxuanzhang.sql.insight.core.exception.DateTypeCastException;
import org.gongxuanzhang.sql.insight.core.object.Row;
import org.gongxuanzhang.sql.insight.core.object.value.Value;
import org.gongxuanzhang.sql.insight.core.object.value.ValueVarchar;

/**
 * there are two ways to result values.
 * fixed value or get column value
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public class StringExpression implements Expression {

    private final String value;

    private final String columnName;

    public StringExpression(String value, String columnName) {
        this.value = value;
        this.columnName = columnName;
    }


    @Override
    public ValueVarchar getExpressionValue(Row row) {
        if (columnName != null) {
            Value result = row.getValueByColumnName(columnName);
            if (result instanceof ValueVarchar) {
                return (ValueVarchar) result;
            }
            throw new DateTypeCastException(result.getSource().toString(), "varchar");

        }
        return new ValueVarchar(value);
    }
}
