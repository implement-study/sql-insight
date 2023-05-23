/*
 * Copyright 2023 java-mysql  and the original author or authors <gongxuanzhangmelt@gmail.com>.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/implement-study/java-mysql/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gongxuanzhang.mysql.entity;

import org.gongxuanzhang.mysql.core.ByteBody;
import org.gongxuanzhang.mysql.entity.page.Compact;
import org.gongxuanzhang.mysql.entity.page.CompactNullValue;
import org.gongxuanzhang.mysql.entity.page.RecordHeader;
import org.gongxuanzhang.mysql.entity.page.RecordHeaderFactory;
import org.gongxuanzhang.mysql.entity.page.UserRecord;
import org.gongxuanzhang.mysql.exception.MySQLException;

import java.util.List;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class InsertRowImpl implements InsertRow {

    private final List<Cell<?>> cellList;

    private TableInfo tableInfo;

    public InsertRowImpl(List<Cell<?>> cellList) {
        this(cellList, null);
    }

    public InsertRowImpl(List<Cell<?>> cellList, TableInfo tableInfo) {
        this.cellList = cellList;
        this.tableInfo = tableInfo;
    }


    @Override
    public <R extends UserRecord> R toUserRecord(Class<R> recordType) throws MySQLException {
        if (recordType == Compact.class) {
            return (R)doToCompact();
        } else {
            throw new MySQLException("还不支持" + recordType.getName() + "行格式");
        }
    }

    private Compact doToCompact() throws MySQLException {
        Compact compact = new Compact();
        CompactNullValue compactNullValue = new CompactNullValue();
        RecordHeader recordHeader = new RecordHeaderFactory().create();

        ByteBody body = new ByteBody();
        for (int i = 0; i < this.cellList.size(); i++) {
            Column column = this.tableInfo.getColumns().get(i);
            Cell<?> cell = this.cellList.get(i);
            if (cell.getValue() == null) {
                compactNullValue.setNull(column.getNullIndex());
            }
            for (byte b : cell.toBytes()) {
                body.add(b);
            }
        }
        compact.setBody(body.toArray());

        return compact;
    }

    @Override
    public List<Cell<?>> getCellList() {
        return this.cellList;
    }

    @Override
    public TableInfo getTableInfo() {
        return this.tableInfo;
    }

    @Override
    public void setTableInfo(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }
}
