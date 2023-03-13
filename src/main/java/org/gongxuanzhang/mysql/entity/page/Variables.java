/*
 * Copyright 2023 java-mysql  and the original author or authors <gongxuanzhangmelt@gmail.com>.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/implement-study/java-mysql/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gongxuanzhang.mysql.entity.page;

import org.gongxuanzhang.mysql.core.ByteSwappable;

import java.nio.ByteBuffer;

/**
 * 变长列表
 *
 * @author gongxuanzhang
 **/
public class Variables implements ByteSwappable<Variables> {


    byte length;

    byte[] varBytes;

    /**
     * 变长列表 第一个字节表示变长列表的长度
     **/
    public int length() {
        return varBytes.length + 1;
    }

    @Override
    public byte[] toBytes() {
        return varBytes;
    }

    @Override
    public Variables fromBytes(byte[] bytes) {
        if (bytes.length == 1) {
            this.length = 1;
            this.varBytes = new byte[0];
            return this;
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        this.length = buffer.get();
        this.varBytes = new byte[this.length];
        buffer.get(this.varBytes);
        return this;
    }
}
