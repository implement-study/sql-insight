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
import org.gongxuanzhang.sql.insight.core.object.value.ValueBoolean;

/**
 * binary operator expression
 * compose left and right
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public abstract class BooleanBinaryOperatorExpression implements BooleanExpression {


    protected final Expression left;
    protected final Expression right;

    protected BooleanBinaryOperatorExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    /**
     * compare between two expression
     * function params is expression not result.
     * because the boolean can lazy invoke.(circuit logic)
     *
     * @return the function
     **/
    protected abstract BooleanOperatorFunction operator();

    @Override
    public Value getExpressionValue(Row row) {
        return new ValueBoolean(operator().apply(left, right, row));
    }
}
