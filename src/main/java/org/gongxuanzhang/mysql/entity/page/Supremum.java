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

import org.gongxuanzhang.mysql.constant.ConstantSize;
import org.gongxuanzhang.mysql.core.ByteSwappable;
import org.gongxuanzhang.mysql.entity.PrimaryKey;
import org.gongxuanzhang.mysql.entity.ShowLength;

import java.nio.ByteBuffer;

/**
 * 上确界
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
public class Supremum implements UserRecord, ShowLength, ByteSwappable {

    public static final String SUPREMUM_BODY = "supremum";

    /**
     * 记录头信息 5字节
     **/
    RecordHeader recordHeader;

    /**
     * 定长8字节 "supremum"
     **/
    byte[] body;


    @Override
    public int length() {
        return ConstantSize.SUPREMUM.getSize();
    }

    @Override
    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(ConstantSize.SUPREMUM.getSize());
        buffer.put(recordHeader.toBytes());
        buffer.put(body);
        return buffer.array();
    }

    @Override
    public String toString() {
        return this.recordHeader.toString() + "[body:" + new String(this.body) + "]";
    }

    @Override
    public RecordHeader getRecordHeader() {
        return this.recordHeader;
    }

    @Override
    public PrimaryKey getPrimaryKey() {
        return null;
    }
}
