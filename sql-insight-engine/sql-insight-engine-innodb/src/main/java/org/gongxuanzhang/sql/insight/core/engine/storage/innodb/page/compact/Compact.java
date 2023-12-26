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

package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact;

import lombok.Data;
import org.gongxuanzhang.easybyte.core.DynamicByteBuffer;
import org.gongxuanzhang.sql.insight.core.annotation.Unused;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.ConstantSize;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.InnodbUserRecord;
import org.gongxuanzhang.sql.insight.core.object.Row;
import org.gongxuanzhang.sql.insight.core.object.Table;
import org.gongxuanzhang.sql.insight.core.object.value.Value;

import java.util.List;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
@Data
public class Compact implements InnodbUserRecord {


    /**
     * variable column list
     **/
    Variables variables;

    /**
     * null list.
     * size is table nullable column count / 8.
     **/
    CompactNullList nullList;

    /**
     * record header 5 bytes
     **/
    RecordHeader recordHeader;

    /**
     * 真实记录
     **/
    byte[] body;

    /**
     * 6字节  唯一标识
     **/
    @Unused
    long rowId;
    /**
     * 事务id  6字节
     **/
    @Unused
    long transactionId;
    /**
     * 7字节，回滚指针
     **/
    @Unused
    long rollPointer;

    Row sourceRow;

    int offsetInPage = -1;


    @Override
    public byte[] rowBytes() {
        DynamicByteBuffer buffer = DynamicByteBuffer.allocate();
        buffer.append(this.variables.toBytes());
        buffer.append(this.nullList.toBytes());
        buffer.append(this.recordHeader.toBytes());
        buffer.append(this.body);
        return buffer.toBytes();
    }


    @Override
    public List<Value> getValues() {
        return sourceRow.getValues();
    }

    @Override
    public long getRowId() {
        return sourceRow.getRowId();
    }

    @Override
    public Value getValueByColumnName(String colName) {
        return sourceRow.getValueByColumnName(colName);
    }

    @Override
    public Table belongTo() {
        return sourceRow.belongTo();
    }

    @Override
    public int length() {
        //    record header must write "ConstantSize.RECORD_HEADER.size()"
        //    because  the compact may come from insert row result in NullPointException
        return variables.length() + nullList.length() + ConstantSize.RECORD_HEADER.size() + body.length;
    }


    @Override
    public int beforeSplitOffset() {
        return variables.length() + nullList.length() + ConstantSize.RECORD_HEADER.size();
    }

    @Override
    public int offset() {
        if (offsetInPage == -1) {
            throw new IllegalArgumentException("unknown offset");
        }
        return offsetInPage;
    }

    @Override
    public int nextRecordOffset() {
        return this.recordHeader.getNextRecordOffset();
    }

    @Override
    public boolean deleteSign() {
        return this.recordHeader.isDelete();
    }

}
