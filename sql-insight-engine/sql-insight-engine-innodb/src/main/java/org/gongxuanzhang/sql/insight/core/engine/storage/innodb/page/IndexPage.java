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

import kotlin.Pair;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.factory.PageFactory;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.index.InnodbIndex;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.IndexRecord;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.RecordHeader;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.RowFormatFactory;
import org.gongxuanzhang.sql.insight.core.object.Column;
import org.gongxuanzhang.sql.insight.core.object.value.Value;
import org.gongxuanzhang.sql.insight.core.object.value.ValueNegotiator;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class IndexPage extends InnoDbPage {

    public IndexPage(InnodbIndex index) {
        super(index);
    }


    @Override
    public void insertData(InnodbUserRecord data) {
        if (data instanceof IndexRecord) {
            super.insertData(data);
            return;
        }
        Pair<InnodbUserRecord, InnodbUserRecord> preAndNext = findPreAndNext(data);
        InnodbUserRecord pre = preAndNext.getFirst();
        InnodbUserRecord next = preAndNext.getSecond();
        IndexRecord hit;
        if (pre instanceof Infimum) {
            hit = (IndexRecord) next;
        } else {
            hit = (IndexRecord) pre;
        }
        InnoDbPage pointPage = PageFactory.findPageByOffset(hit.indexNode().getPointer(), this.ext.belongIndex);
        pointPage.insertData(data);
    }

    @Override
    protected IndexRecord wrapUserRecord(int offsetInPage) {
        //  todo dynamic primary key
        List<Column> columns = this.ext.belongIndex.columns();
        RecordHeader recordHeader = RowFormatFactory.readRecordHeader(this, offsetInPage);
        Value[] key = new Value[columns.size()];
        ByteBuffer buffer = ByteBuffer.wrap(this.toBytes(), offsetInPage, this.length() - offsetInPage);
        for (int i = 0; i < key.length; i++) {
            Column column = columns.get(i);
            byte[] valueArr = new byte[column.getDataType().getLength()];
            buffer.get(valueArr);
            key[i] = ValueNegotiator.wrapValue(column, valueArr);
        }
        return new IndexRecord(recordHeader, new IndexNode(key, buffer.getInt()), this.ext.belongIndex);
    }


    /**
     * data page will split when free space less than one in thirty-two page size
     **/
    @Override
    protected void splitIfNecessary() {
        if (this.getFreeSpace() > ConstantSize.PAGE.size() >> 5) {
            return;
        }
        List<InnodbUserRecord> allRecords = new ArrayList<>(this.pageHeader.recordCount + 1);
        InnodbUserRecord base = this.infimum;
        while (true) {
            base = getUserRecordByOffset(base.offset() + base.nextRecordOffset());
            if (base == this.supremum) {
                break;
            }
            allRecords.add(base);
        }
        InnoDbPage pre = PageFactory.createIndexPage(allRecords.subList(0, allRecords.size() / 2), ext.belongIndex);
        InnoDbPage next = PageFactory.createIndexPage(allRecords.subList(allRecords.size() / 2, allRecords.size()),
                ext.belongIndex);
        upgrade(pre, next);

    }

    @Override
    public IndexRecord pageIndex() {
        IndexRecord firstData = (IndexRecord) getUserRecordByOffset(infimum.offset() + infimum.nextRecordOffset());
        IndexNode node = new IndexNode(firstData.indexNode().getKey(), this.fileHeader.offset);
        return new IndexRecord(node, this.ext.belongIndex);
    }


    @Override
    public int compare(InnodbUserRecord o1, InnodbUserRecord o2) {
        if (!(o1 instanceof IndexRecord)) {
            throw new IllegalArgumentException("index page only support compare index record");
        }
        Value[] values1 = ((IndexRecord) o1).indexNode().getKey();
        Value[] values2 = ((IndexRecord) o2).indexNode().getKey();
        for (int i = 0; i < values1.length; i++) {
            int compare = values1[i].compareTo(values2[i]);
            if (compare != 0) {
                return compare;
            }
        }
        return 0;
    }
}
