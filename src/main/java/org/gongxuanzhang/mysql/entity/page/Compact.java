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
import org.gongxuanzhang.mysql.annotation.NotInPage;
import org.gongxuanzhang.mysql.entity.Column;
import org.gongxuanzhang.mysql.entity.IntegerPrimaryKey;
import org.gongxuanzhang.mysql.entity.PrimaryKey;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.entity.VarcharPrimaryKey;
import org.gongxuanzhang.mysql.tool.BitUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * compact行格式
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
@Data
public class Compact implements UserRecord {

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


    @NotInPage("表示记录在页中的偏移量，并不在页中真实存储")
    private short pageOffset;

    @NotInPage("主键是通过计算得来的")
    private PrimaryKey primaryKey;

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


    @Override
    public int pageOffset() {
        return this.pageOffset;
    }

    @Override
    public PrimaryKey getPrimaryKey(TableInfo tableInfo) {
        if (primaryKey != null) {
            return primaryKey;
        }
        primaryKey = calculatePrimaryKey(tableInfo);
        return primaryKey;
    }

    private PrimaryKey calculatePrimaryKey(TableInfo tableInfo) {
        int[] primaryKeyIndex = tableInfo.getPrimaryKeyIndex();
        if (primaryKeyIndex.length == 0) {
            throw new UnsupportedOperationException("暂不支持无主键");
        }
        if (primaryKeyIndex.length == 1) {
            int primaryLength;
            Column primaryCol = tableInfo.getColumns().get(0);
            if (primaryCol.isDynamic()) {
                primaryLength = this.getVariables().get(0);
            } else {
                primaryLength = primaryCol.getLength();
            }
            byte[] primaryBody = Arrays.copyOfRange(this.getBody(), 0, primaryLength);
            switch (primaryCol.getType()) {
                case INT:
                    return new IntegerPrimaryKey(BitUtils.joinInt(primaryBody));
                case VARCHAR:
                    return new VarcharPrimaryKey(new String(primaryBody));
                default:
                    throw new UnsupportedOperationException("不支持" + primaryCol.getType() + "类型主键");
            }
        } else {
            throw new UnsupportedOperationException("暂不支持联合主键");
        }
    }


    @Override
    public String toString() {
        return String.format("Compact: 主键[%s]", this.primaryKey);
    }
}

