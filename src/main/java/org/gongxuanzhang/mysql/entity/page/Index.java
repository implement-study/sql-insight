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

package org.gongxuanzhang.mysql.entity.page;

import lombok.Data;
import org.gongxuanzhang.mysql.core.ByteSwappable;

import java.nio.ByteBuffer;

/**
 * 索引行格式
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
@Data
public class Index implements UserRecord, ByteSwappable {


    RecordHeader recordHeader;

    /**
     * 索引长度
     **/
    short indexLength;

    /**
     * 索引内容
     **/
    byte[] indexBody;

    /**
     * 这一页对应的偏移量
     **/
    int pageOffset;

    @Override
    public byte[] toBytes() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(this.length());
        byteBuffer.put(recordHeader.toBytes());
        byteBuffer.putShort(indexLength);
        byteBuffer.put(indexBody);
        return byteBuffer.array();
    }

    @Override
    public RecordHeader getRecordHeader() {
        return this.recordHeader;
    }


    @Override
    public int length() {
        return this.recordHeader.length() + 2 + indexBody.length;
    }
}
