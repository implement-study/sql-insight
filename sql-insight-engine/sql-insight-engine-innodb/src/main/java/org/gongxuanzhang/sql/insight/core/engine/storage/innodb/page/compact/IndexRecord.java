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

import org.gongxuanzhang.easybyte.core.DynamicByteBuffer;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.IndexNode;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.InnodbUserRecord;
import org.gongxuanzhang.sql.insight.core.object.Index;
import org.gongxuanzhang.sql.insight.core.object.Table;
import org.gongxuanzhang.sql.insight.core.object.value.Value;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class IndexRecord implements InnodbUserRecord {


    private final RecordHeader recordHeader;

    private final Index index;

    private final IndexNode indexNode;

    private int offsetInPage = -1;

    public IndexRecord(IndexNode indexNode, Index index) {
        this(new IndexHeader(), indexNode, index);
    }

    public IndexRecord(RecordHeader recordHeader, IndexNode indexNode, Index index) {
        this.recordHeader = recordHeader;
        this.index = index;
        this.indexNode = indexNode;
    }


    /**
     * a index record body is a index node
     **/
    public IndexNode indexNode() {
        return this.indexNode;
    }

    @Override
    public List<Value> getValues() {
        return Arrays.asList(indexNode.getKey());
    }

    @Override
    public long getRowId() {
        return -1;
    }

    @Override
    public Value getValueByColumnName(String colName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Table belongTo() {
        return this.index.belongTo();
    }


    @NotNull
    @Override
    public RecordHeader getRecordHeader() {
        return this.recordHeader;
    }

    @Override
    public int beforeSplitOffset() {
        return this.recordHeader.length();
    }


    @Override
    public boolean deleteSign() {
        return this.recordHeader.isDelete();
    }


    @Override
    public byte[] rowBytes() {
        DynamicByteBuffer buffer = DynamicByteBuffer.wrap(recordHeader.toBytes());
        buffer.append(this.indexNode.toBytes());
        return buffer.toBytes();
    }

    @Override
    public int offset() {
        if (offsetInPage == -1) {
            throw new IllegalArgumentException("unknown offset");
        }
        return offsetInPage;
    }

    @Override
    public void setOffset(int offset) {
        this.offsetInPage = offset;
    }

    @Override
    public int nextRecordOffset() {
        return this.recordHeader.getNextRecordOffset();
    }


    @Override
    public int length() {
        return this.recordHeader.length() + indexNode.length();
    }
}
