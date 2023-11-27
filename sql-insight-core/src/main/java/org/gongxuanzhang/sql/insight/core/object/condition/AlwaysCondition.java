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
import org.gongxuanzhang.sql.insight.core.object.value.ValueBoolean;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class AlwaysCondition implements BooleanExpression {

    private static final AlwaysCondition TURE_INSTANCE = new AlwaysCondition(true);

    private static final AlwaysCondition FALSE_INSTANCE = new AlwaysCondition(false);

    private static final ValueBoolean TRUE_VALUE = new ValueBoolean(true);

    private static final ValueBoolean FALSE_VALUE = new ValueBoolean(false);

    private final boolean bool;

    public static AlwaysCondition getInstance(boolean bool) {
        if (bool) {
            return TURE_INSTANCE;
        }
        return FALSE_INSTANCE;
    }

    private AlwaysCondition(boolean bool) {
        this.bool = bool;
    }


    @Override
    public ValueBoolean getExpressionValue(Row row) {
        if (bool) {
            return TRUE_VALUE;
        }
        return FALSE_VALUE;
    }
}
