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

import org.gongxuanzhang.mysql.constant.ConstantSize;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.tool.BitUtils;

import java.nio.ByteBuffer;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class CompactSwapper implements ByteBeanSwapper<Compact> {

    private final TableInfo tableInfo;

    public CompactSwapper(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }

    @Override
    public Compact swap(byte[] bytes) {
        Compact compact = new Compact();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        byte[] headBuffer = new byte[ConstantSize.RECORD_HEADER.getSize()];
        buffer.get(headBuffer);
        compact.recordHeader = new RecordHeader(headBuffer);
        byte varLength = tableInfo.getVariableCount().byteValue();
        if (varLength == 0) {
            compact.variables = new Variables(new byte[0]);
        } else {
            byte[] varBytes = new byte[varLength];
            buffer.get(varBytes);
            compact.variables = new Variables(varBytes);
        }
        compact.nullValues = new CompactNullValue(buffer.getShort());
        byte[] candidateBuffer = new byte[6];
        buffer.get(candidateBuffer);
        compact.rowId = BitUtils.joinLong(candidateBuffer);
        buffer.get(candidateBuffer);
        compact.transactionId = BitUtils.joinLong(candidateBuffer);
        candidateBuffer = new byte[7];
        buffer.get(candidateBuffer);
        compact.rollPointer = BitUtils.joinLong(candidateBuffer);
        compact.body = new byte[buffer.remaining()];
        buffer.get(compact.body);
        return compact;
    }

}
