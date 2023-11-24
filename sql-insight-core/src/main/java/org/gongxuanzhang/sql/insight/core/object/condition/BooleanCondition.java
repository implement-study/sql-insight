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

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class BooleanCondition implements Condition {

    private final boolean and;
    private final Condition left;
    private final Condition right;

    public BooleanCondition(boolean and, Condition left, Condition right) {
        this.and = and;
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean hit(Row row) {
        if (and) {
            return left.hit(row) && right.hit(row);
        }
        return left.hit(row) || right.hit(row);
    }
}
