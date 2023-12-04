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

import org.gongxuanzhang.sql.insight.core.object.Row;
import org.gongxuanzhang.sql.insight.core.object.value.Value;
import org.gongxuanzhang.sql.insight.core.object.value.ValueInt;

/**
 * calc two value to one value.
 * must be number
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public abstract class NumberOperatorExpression implements Expression {


    protected final Expression left;
    protected final Expression right;

    protected NumberOperatorExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    /**
     * calculate a result from left and right value
     *
     * @return the function
     **/
    protected abstract NumberValueOperatorFunction operator();

    protected abstract char operatorDesc();

    @Override
    public Value getExpressionValue(Row row) {
        Value leftValue = left.getExpressionValue(row);
        Value rightValue = right.getExpressionValue(row);
        if (!(leftValue instanceof ValueInt) || !(rightValue instanceof ValueInt)) {
            throw new UnsupportedOperationException(operatorDesc() + " must be pair of number ");
        }
        return operator().apply((ValueInt) leftValue, (ValueInt) rightValue);
    }
}
