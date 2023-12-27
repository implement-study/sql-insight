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

import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.index.InnodbIndex;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.IndexRecord;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.RecordHeader;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.RowFormatFactory;
import org.gongxuanzhang.sql.insight.core.object.Column;
import org.gongxuanzhang.sql.insight.core.object.value.Value;
import org.gongxuanzhang.sql.insight.core.object.value.ValueNegotiator;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class IndexPage extends InnoDbPage {

    public IndexPage(InnodbIndex index) {
        super(index);
    }


    @Override
    protected IndexRecord wrapUserRecord(int offsetInPage) {
        //  todo dynamic primary key
        List<Column> columns = this.ext.belongIndex.columns();
        RecordHeader recordHeader = RowFormatFactory.readRecordHeader(this, offsetInPage);
        Value[] key = new Value[columns.size()];
        ByteBuffer buffer = ByteBuffer.wrap(this.ext.source, offsetInPage, this.ext.source.length - offsetInPage);
        for (int i = 0; i < key.length; i++) {
            Column column = columns.get(i);
            byte[] valueArr = new byte[column.getDataType().getLength()];
            buffer.get(valueArr);
            key[i] = ValueNegotiator.wrapValue(column, valueArr);
        }
        return new IndexRecord(recordHeader, new IndexNode(key, buffer.getInt()), this.ext.belongIndex);
    }

    @Override
    protected void splitIfNecessary() {
        //  index page split
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
