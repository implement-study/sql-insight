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

package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page;

import org.gongxuanzhang.easybyte.core.DynamicByteBuffer;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.RecordHeader;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.SupremumHeader;
import org.gongxuanzhang.sql.insight.core.object.Table;
import org.gongxuanzhang.sql.insight.core.object.value.Value;

import java.util.List;

/**
 * max record in group
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/

public class Supremum implements InnodbUserRecord {

    public static final String SUPREMUM_BODY = "supremum";


    /**
     * 5 bytes
     **/
    RecordHeader recordHeader;

    /**
     * 8 bytes as "supremum"
     **/
    final byte[] body = SUPREMUM_BODY.getBytes();

    public Supremum() {
        this.recordHeader = new SupremumHeader();
    }

    @Override
    public byte[] rowBytes() {
        return DynamicByteBuffer.wrap(recordHeader.toBytes()).append(this.body).toBytes();
    }


    @Override
    public String toString() {
        return this.recordHeader.toString() + "[body:" + new String(this.body) + "]";
    }


    @Override
    public List<Value> getValues() {
        return supremumUnsupport();
    }

    @Override
    public long getRowId() {
        return Long.MAX_VALUE;
    }

    @Override
    public Value getValueByColumnName(String colName) {
        return supremumUnsupport();
    }


    @Override
    public int offset() {
        return ConstantSize.SUPREMUM.offset();
    }

    @Override
    public void setOffset(int offset) {
        throw new UnsupportedOperationException("supremum can't set offset ");
    }


    @Override
    public int nextRecordOffset() {
        return 0;
    }

    @Override
    public boolean deleteSign() {
        return false;
    }

    @Override
    public Table belongTo() {
        return supremumUnsupport();
    }

    public Supremum setRecordHeader(RecordHeader recordHeader) {
        this.recordHeader = recordHeader;
        return this;
    }

    private <T> T supremumUnsupport() {
        throw new UnsupportedOperationException("this is supremum!");
    }

    @Override
    public RecordHeader getRecordHeader() {
        return recordHeader;
    }

    @Override
    public int beforeSplitOffset() {
        return recordHeader.length();
    }

    @Override
    public int length() {
        return ConstantSize.SUPREMUM.size();
    }
}
