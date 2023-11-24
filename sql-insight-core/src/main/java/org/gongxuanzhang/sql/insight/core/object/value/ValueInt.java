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
import org.gongxuanzhang.easybyte.core.tool.ByteArrays;
import org.gongxuanzhang.sql.insight.core.exception.DateTypeCastException;
import org.jetbrains.annotations.NotNull;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
@EqualsAndHashCode
public class ValueInt extends BaseValue {

    private final int value;

    public ValueInt(int value) {
        this.value = value;
    }

    public ValueInt(Value value) {
        try {
            this.value = Integer.parseInt(value.getSource().toString());
        } catch (Exception e) {
            throw new DateTypeCastException("int", value.getClass().getName());
        }
    }

    @Override
    public Integer getSource() {
        return value;
    }

    @Override
    public int getLength() {
        return Integer.BYTES;
    }


    @Override
    public byte[] toBytes() {
        return ByteArrays.fromInt(this.value);
    }

    @Override
    public int compareTo(@NotNull Value o) {
        if (o instanceof ValueInt) {
            return Integer.compare(this.value, ((ValueInt) o).value);
        }
        throw new IllegalArgumentException("ValueInt can't compare to " + o.getClass().getName());
    }
}
