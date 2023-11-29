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
import org.gongxuanzhang.sql.insight.core.object.value.ValueBoolean;
import org.gongxuanzhang.sql.insight.core.object.value.ValueInt;
import org.gongxuanzhang.sql.insight.core.object.value.ValueVarchar;

import java.util.Objects;

/**
 * you can calculate the result with row
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public interface Expression {

    /**
     * expression and row  calculate
     *
     * @return value
     **/
    Value getExpressionValue(Row row);


    /**
     * value to boolean support expression
     **/
    default Boolean getBooleanValue(Row row) {
        Value expressionValue = getExpressionValue(row);
        if (expressionValue instanceof ValueBoolean) {
            return ((ValueBoolean) expressionValue).getSource();
        }
        if (expressionValue instanceof ValueInt) {
            int value = ((ValueInt) expressionValue).getSource();
            return !Objects.equals(value, 0);
        }
        if (expressionValue instanceof ValueVarchar) {
            String string = expressionValue.getSource().toString();
            return (!string.isEmpty());
        }
        throw new DateTypeCastException("boolean", expressionValue.getSource().getClass().getName());
    }
}
