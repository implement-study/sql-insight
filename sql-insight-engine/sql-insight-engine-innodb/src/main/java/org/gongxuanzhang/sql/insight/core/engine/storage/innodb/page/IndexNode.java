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

package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page;

import lombok.Data;
import org.gongxuanzhang.easybyte.core.ByteWrapper;
import org.gongxuanzhang.easybyte.core.DynamicByteBuffer;
import org.gongxuanzhang.sql.insight.core.object.value.Value;


/**
 * index page contains some index node.
 * a index node contains a pointer and index key.
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
@Data
public class IndexNode implements PageObject, ByteWrapper {

    private final int length;

    private final Value[] key;

    private final int pointer;


    public IndexNode(Value[] key, int pointer) {
        this.key = key;
        int candidate = 0;
        for (Value value : key) {
            candidate += value.getLength();
        }
        this.length = candidate;
        this.pointer = pointer;
    }

    @Override
    public int length() {
        return key.length + Integer.BYTES;
    }

    @Override
    public byte[] toBytes() {
        DynamicByteBuffer buffer = DynamicByteBuffer.allocate();
        for (Value value : this.key) {
            buffer.append(value.toBytes());
        }
        buffer.appendInt(pointer);
        return buffer.toBytes();
    }
}
