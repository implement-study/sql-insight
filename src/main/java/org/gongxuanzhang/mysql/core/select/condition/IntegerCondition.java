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

package org.gongxuanzhang.mysql.core.select.condition;

import org.gongxuanzhang.mysql.core.Expression;

import java.util.function.IntFunction;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class IntegerCondition implements Expression<Integer> {

    private final IntFunction<Integer> intFunction;

    private final int value;

    public IntegerCondition(int value, IntFunction<Integer> intFunction) {
        this.intFunction = intFunction;
        this.value = value;
    }

    public IntegerCondition(int value) {
        this.intFunction = null;
        this.value = value;
    }

    @Override
    public Integer getValue() {
        if (intFunction == null) {
            return value;
        }
        return intFunction.apply(value);
    }
}
