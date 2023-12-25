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

import org.gongxuanzhang.easybyte.core.DynamicByteBuffer;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.RecordHeader;
import org.gongxuanzhang.sql.insight.core.exception.UnknownColumnException;
import org.gongxuanzhang.sql.insight.core.object.Column;
import org.gongxuanzhang.sql.insight.core.object.Table;
import org.gongxuanzhang.sql.insight.core.object.value.Value;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * cluster index page user record
 * have a primary key and data page offset
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public class ClusterdIndexRecord implements InnodbUserRecord {

    private RecordHeader recordHeader;

    private final Table table;

    private final Column primaryKey;

    private final Value primaryKeyValue;

    private final int pageOffset;

    public ClusterdIndexRecord(InnodbUserRecord dataRecord, InnoDbPage dataPage) {
        table = dataRecord.belongTo();
        List<Column> columnList = table.getColumnList();
        this.primaryKey = columnList.get(table.getExt().getPrimaryKeyIndex());
        this.primaryKeyValue = dataRecord.getValueByColumnName(primaryKey.getName());
        this.pageOffset = dataPage.fileHeader.offset;
    }

    @Override
    public List<Value> getValues() {
        return Collections.singletonList(primaryKeyValue);
    }

    @Override
    public long getRowId() {
        return -1;
    }

    @Override
    public Value getValueByColumnName(String colName) {
        if (!Objects.equals(colName, primaryKey.getName())) {
            throw new UnknownColumnException(colName);
        }
        return primaryKeyValue;
    }

    @Override
    public Table belongTo() {
        return this.table;
    }

    @Override
    public byte[] rowBytes() {
        DynamicByteBuffer buffer = DynamicByteBuffer.wrap(recordHeader.toBytes());
        buffer.append(this.primaryKeyValue.toBytes());
        buffer.appendInt(this.pageOffset);
        return buffer.toBytes();
    }

    @Override
    public int offset() {
        return this.pageOffset;
    }

    @Override
    public int nextRecordOffset() {
        return this.recordHeader.getNextRecordOffset();
    }

    @Override
    public boolean deleteSign() {
        return false;
    }

    @Override
    public RecordHeader getRecordHeader() {
        return this.recordHeader;
    }

    @Override
    public int length() {
        return this.recordHeader.length() + primaryKeyValue.getLength() + Integer.BYTES;
    }
}
