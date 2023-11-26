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


import com.google.common.base.Strings;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
@EqualsAndHashCode
public class ValueChar implements Value {

    private final String value;

    private final int length;

    public ValueChar(String value, int length) {
        if (value.length() < length) {
            value += Strings.repeat(" ", length - value.length());
        }
        this.value = value;
        this.length = length;
    }


    @Override
    public int getLength() {
        return this.length;
    }

    @Override
    public String getSource() {
        return value;
    }

    @Override
    public byte[] toBytes() {
        return this.value.getBytes();
    }

    @Override
    public int compareTo(@NotNull Value o) {
        return this.value.compareTo(o.getSource().toString());
    }
}
