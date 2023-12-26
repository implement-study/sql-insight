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

import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.InnodbUserRecord;
import org.gongxuanzhang.sql.insight.core.object.value.Value;

import java.util.List;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public interface IndexRecord extends InnodbUserRecord {


    @Override
    default List<Value> getValues() {
        throw new UnsupportedOperationException("index record not support ");
    }

    @Override
    default long getRowId() {
        return -1;
    }

    @Override
    default Value getValueByColumnName(String colName) {
        throw new UnsupportedOperationException("index record not support ");
    }


    @Override
    default int beforeSplitOffset() {
        return getRecordHeader().length();
    }


    @Override
    default boolean deleteSign() {
        return false;
    }


    @Override
    default int nextRecordOffset() {
        return this.getRecordHeader().getNextRecordOffset();
    }


}
