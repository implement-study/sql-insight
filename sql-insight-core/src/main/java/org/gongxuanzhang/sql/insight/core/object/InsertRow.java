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

package org.gongxuanzhang.sql.insight.core.object;

import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import org.gongxuanzhang.sql.insight.core.exception.InsertException;
import org.gongxuanzhang.sql.insight.core.object.value.Value;
import org.gongxuanzhang.sql.insight.core.object.value.ValueChar;
import org.gongxuanzhang.sql.insight.core.object.value.ValueInt;
import org.gongxuanzhang.sql.insight.core.object.value.ValueVarchar;

import java.util.ArrayList;
import java.util.List;


/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class InsertRow implements Row, FillDataVisitor, TableContainer {

    private Table table;

    private final List<Column> insertColumns;

    private final List<Value> valueList = new ArrayList<>();

    private final long insertRowId;

    public InsertRow(List<Column> insertColumns, long insertRowId) {
        this.insertColumns = insertColumns;
        this.insertRowId = insertRowId;
    }


    @Override
    public List<Value> getValues() {
        return valueList;
    }

    @Override
    public long getRowId() {
        return this.insertRowId;
    }

    @Override
    public void endVisit(SQLIntegerExpr x) {
        int value = x.getNumber().intValue();
        DataType.Type currentType = currentColumn().getDataType().getType();
        if (currentType != DataType.Type.INT) {
            throw new InsertException(this.insertRowId, "number " + value + " can't cast to " + currentType);
        }
        valueList.add(new ValueInt(value));
    }

    @Override
    public void endVisit(SQLCharExpr x) {
        String text = x.getText();
        Column column = currentColumn();
        DataType dataType = column.getDataType();
        switch (dataType.getType()) {
            case VARCHAR:
                valueList.add(wrapVarchar(text));
                break;
            case CHAR:
                valueList.add(wrapChar(text));
                break;
            default:
                throw new InsertException(this.insertRowId, text + " can't cast to " + dataType.getType());

        }
    }

    private ValueVarchar wrapVarchar(String text) {
        Column column = currentColumn();
        int length = column.getDataType().getLength();
        if (text.length() > length) {
            throw new InsertException(this.insertRowId, "Data too long for column " + column.getName());
        }
        return new ValueVarchar(text);
    }

    private ValueChar wrapChar(String text) {
        Column column = currentColumn();
        int length = column.getDataType().getLength();
        if (text.getBytes().length > length) {
            throw new InsertException(this.insertRowId, "Data too long for column " + column.getName());
        }
        return new ValueChar(text, length);
    }

    /**
     * return current visit values target column.
     * use before current add.
     **/
    private Column currentColumn() {
        return insertColumns.get(valueList.size());
    }


    @Override
    public Table getTable() {
        return this.table;
    }

    @Override
    public void setTable(Table table) {
        this.table = table;
    }
}
