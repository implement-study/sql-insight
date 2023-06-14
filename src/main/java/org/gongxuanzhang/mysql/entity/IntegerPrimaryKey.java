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

package org.gongxuanzhang.mysql.entity;

import org.jetbrains.annotations.NotNull;


/**
 * 整数主键
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class IntegerPrimaryKey implements PrimaryKey {

    private final int value;

    public IntegerPrimaryKey(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int compareTo(@NotNull PrimaryKey other) {
        if (other instanceof SupremumPrimaryKey) {
            return -1;
        }
        if (other instanceof InfimumPrimaryKey) {
            return 1;
        }
        if (!(other instanceof IntegerPrimaryKey)) {
            throw new IllegalArgumentException("主键异常");
        }
        return Integer.compare(this.value, ((IntegerPrimaryKey) other).value);
    }


    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
