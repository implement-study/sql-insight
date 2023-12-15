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

import org.gongxuanzhang.sql.insight.core.exception.SqlInsightException;
import org.gongxuanzhang.sql.insight.core.object.Column;
import org.gongxuanzhang.sql.insight.core.object.InsertRow;
import org.gongxuanzhang.sql.insight.core.object.value.Value;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class RowFormatFactory {

    private RowFormatFactory() {

    }

    /**
     * create row format from insert row.
     * the record header not adjust
     **/
    public static Compact fromInsertRow(InsertRow row) {
        Compact compact = new Compact();
        compact.variables = new Variables();
        compact.nullValues = new CompactNullValue();
        compact.recordHeader = new RecordHeader();
        compact.sourceRow = row;
        for (InsertRow.InsertItem insertItem : row) {
            Column column = insertItem.getColumn();
            Value value = insertItem.getValue();
            int index = insertItem.getIndex();
            if (value.getSource() == null && column.isNotNull()) {
                throw new SqlInsightException(column.getName() + " must not null");
            }
            if (value.getSource() == null) {
                compact.nullValues.setNull(index);
                continue;
            }
            if (column.isVariable()) {
                int length = value.getLength();
                if (length >= Math.pow(2, Byte.SIZE)) {
                    throw new SqlInsightException("length too long ");
                }
                compact.variables.addVariableLength((byte) value.getLength());
            }
        }
        return compact;
    }


}
