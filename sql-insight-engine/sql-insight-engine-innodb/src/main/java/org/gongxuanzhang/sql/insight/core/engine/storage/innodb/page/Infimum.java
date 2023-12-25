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
import org.gongxuanzhang.sql.insight.core.object.Table;
import org.gongxuanzhang.sql.insight.core.object.value.Value;

import java.util.List;


/**
 * min record in group
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
public class Infimum implements InnodbUserRecord {

    private static final String INFIMUM_BODY = "infimum";

    /**
     * 5 bytes.
     **/
    RecordHeader recordHeader;

    /**
     * fixed 8 bytes. "infimum" is 7 bytes . fill 0 zero occupy the space
     **/
    final byte[] body;

    public Infimum() {
        body = DynamicByteBuffer.wrap(INFIMUM_BODY.getBytes()).append((byte) 0).toBytes();
    }

    @Override
    public byte[] rowBytes() {
        return DynamicByteBuffer.wrap(recordHeader.toBytes()).append(this.body).toBytes();
    }

    @Override
    public int nextRecordOffset() {
        return recordHeader.getNextRecordOffset();
    }

    @Override
    public boolean deleteSign() {
        return false;
    }

    @Override
    public int offset() {
        return ConstantSize.INFIMUM.offset();
    }


    @Override
    public String toString() {
        return this.recordHeader.toString() + "[body:" + new String(this.body) + "]";
    }

    @Override
    public List<Value> getValues() {
        return infimumUnsupport();
    }

    @Override
    public long getRowId() {
        return infimumUnsupport();
    }

    @Override
    public Value getValueByColumnName(String colName) {
        return infimumUnsupport();
    }

    @Override
    public Table belongTo() {
        return infimumUnsupport();
    }

    private <T> T infimumUnsupport() {
        throw new UnsupportedOperationException("this is infimum!");
    }

    @Override
    public RecordHeader getRecordHeader() {
        return this.recordHeader;
    }

    @Override
    public int length() {
        return ConstantSize.INFIMUM.size();
    }
}
