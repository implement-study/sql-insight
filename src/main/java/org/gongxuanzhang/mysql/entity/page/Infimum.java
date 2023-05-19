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


import lombok.Data;
import org.gongxuanzhang.mysql.constant.ConstantSize;
import org.gongxuanzhang.mysql.core.ByteSwappable;
import org.gongxuanzhang.mysql.entity.ShowLength;

import java.nio.ByteBuffer;


/**
 * 下确界
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
@Data
public class Infimum implements ByteSwappable, ShowLength {

    public static final String INFIMUM_BODY = "infimum";

    /**
     * 记录头信息 5字节
     **/
    RecordHeader recordHeader;

    /**
     * 定长8字节 "infimum" 这是7字节 最后一字节是0
     **/
    byte[] body;


    @Override
    public int length() {
        return ConstantSize.INFIMUM.getSize();
    }

    @Override
    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(ConstantSize.INFIMUM.getSize());
        byte[] recordHeaderBytes = recordHeader.toBytes();
        buffer.put(recordHeaderBytes);
        buffer.put(this.body);
        return buffer.array();
    }

    @Override
    public String toString() {
        return this.recordHeader.toString() + "[body:" + new String(this.body) + "]";
    }
}
