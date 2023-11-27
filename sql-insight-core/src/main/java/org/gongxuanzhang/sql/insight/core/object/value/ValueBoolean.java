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


import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
@EqualsAndHashCode
public class ValueBoolean extends BaseValue {

    private final boolean value;

    public ValueBoolean(boolean value) {
        this.value = value;
    }


    @Override
    public Boolean getSource() {
        return this.value;
    }

    @Override
    public int getLength() {
        return Integer.BYTES;
    }


    @Override
    public byte[] toBytes() {
        return new byte[]{(byte) (this.value ? 1 : 0)};
    }

    @Override
    public int compareTo(@NotNull Value o) {
        throw new IllegalArgumentException("ValueBoolean can't compare to " + o.getClass().getName());
    }
}
