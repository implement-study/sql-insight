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
import org.gongxuanzhang.sql.insight.core.annotation.Temporary;

import java.nio.ByteBuffer;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
@EqualsAndHashCode
public class ValueInt implements Value {

    private final int value;

    public ValueInt(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int getLength() {
        return Integer.BYTES;
    }


    @Temporary
    @Override
    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(this.value);
        return buffer.array();
    }

}