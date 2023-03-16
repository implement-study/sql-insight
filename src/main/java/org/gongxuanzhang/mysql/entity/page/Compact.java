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
import org.gongxuanzhang.mysql.core.ByteSwappable;
import org.gongxuanzhang.mysql.entity.ShowLength;
import org.gongxuanzhang.mysql.tool.BitUtils;

import java.nio.ByteBuffer;

/**
 * compact行格式
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
@Data
public class Compact implements UserRecord, ByteSwappable, ShowLength {

    /**
     * 记录头信息 5字节
     **/
    RecordHeader recordHeader;

    /**
     * 变长字段信息
     **/
    Variables variables;

    /**
     * null值列表 默认两字节
     **/
    CompactNullValue nullValues;
    /**
     * 6字节  唯一标识
     **/
    long rowId;
    /**
     * 事务id  6字节
     **/
    long transactionId;
    /**
     * 7字节，回滚指针
     **/
    long rollPointer;
    /**
     * 真实记录
     **/
    byte[] body;


    @Override
    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(length());
        buffer.put(recordHeader.toBytes());
        buffer.put(variables.toBytes());
        buffer.put(nullValues.toBytes());
        buffer.put(BitUtils.cutToByteArray(rowId, 6));
        buffer.put(BitUtils.cutToByteArray(transactionId, 6));
        buffer.put(BitUtils.cutToByteArray(rollPointer, 7));
        buffer.put(body);
        return buffer.array();
    }


    @Override
    public int length() {
        return body.length + variables.length() + nullValues.length()
                //  record_head
                + 5
                //  txId
                + 6
                //  rowId
                + 6
                //  rollPointer
                + 7;
    }


}

