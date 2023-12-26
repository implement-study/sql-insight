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
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.IndexHeader;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.IndexRecord;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.RecordHeader;
import org.gongxuanzhang.sql.insight.core.object.Index;
import org.gongxuanzhang.sql.insight.core.object.Row;
import org.gongxuanzhang.sql.insight.core.object.Table;
import org.gongxuanzhang.sql.insight.core.object.value.Value;
import org.jetbrains.annotations.NotNull;

/**
 * unique column only one.
 * have a primary key(or other) and data page offset
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public class SingleIndexRecord implements IndexRecord {

    private final RecordHeader recordHeader;

    private final Index index;

    private final Value uniqueValue;

    /**
     * may be data page offset or primary key or other index page offset
     **/
    private final int pointer;

    private int offsetInPage = -1;

    public SingleIndexRecord(SingleIndexRecord childIndex) {
        this.recordHeader = new IndexHeader();
        this.index = childIndex.index;
        this.uniqueValue = childIndex.uniqueValue;
        this.pointer = childIndex.offset();
    }

    public SingleIndexRecord(InnoDbPage dataPage, Value uniqueValue) {
        this.recordHeader = new IndexHeader();
        this.index = dataPage.belongIndex;
        this.uniqueValue = uniqueValue;
        this.pointer = dataPage.fileHeader.offset;
    }


    @Override
    public Table belongTo() {
        return this.index.belongTo();
    }

    @Override
    public byte[] rowBytes() {
        DynamicByteBuffer buffer = DynamicByteBuffer.wrap(recordHeader.toBytes());
        buffer.append(this.uniqueValue.toBytes());
        buffer.appendInt(this.pointer);
        return buffer.toBytes();
    }


    @Override
    public int offset() {
        if (offsetInPage == -1) {
            throw new IllegalArgumentException("unknown offset");
        }
        return offsetInPage;
    }

    public void setOffsetInPage(int offsetInPage) {
        this.offsetInPage = offsetInPage;
    }

    @Override
    public RecordHeader getRecordHeader() {
        return this.recordHeader;
    }

    @Override
    public int length() {
        return this.recordHeader.length() + uniqueValue.getLength() + Integer.BYTES;
    }

    @Override
    public int compareTo(@NotNull Row that) {
        if (!(that instanceof SingleIndexRecord)) {
            throw new IllegalArgumentException();
        }
        if (((SingleIndexRecord) that).index != this.index) {
            throw new IllegalArgumentException();
        }
        return this.uniqueValue.compareTo(((SingleIndexRecord) that).uniqueValue);
    }
}
