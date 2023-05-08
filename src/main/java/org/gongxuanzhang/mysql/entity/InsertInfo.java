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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import lombok.Data;
import org.gongxuanzhang.mysql.core.TableInfoBox;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.tool.CollectionUtils;
import org.gongxuanzhang.mysql.tool.SqlUtils;
import org.gongxuanzhang.mysql.tool.TableInfoUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * insert into info
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class InsertInfo implements ExecuteInfo, TableInfoBox {

    private TableInfo tableInfo;

    /**
     * 保存解析之后的 所有列的所有数据  包括null
     **/
    private List<List<Cell<?>>> insertData;


    public InsertInfo(MySqlInsertStatement insertStatement) throws MySQLException {
        this.tableInfo = TableInfoUtils.selectTableInfo(insertStatement.getTableName().toString());
        fillInsertColumns(insertStatement);
        System.out.println(1);
    }

    private void fillInsertColumns(MySqlInsertStatement insertStatement) throws MySQLException {
        List<Column> allColumns = tableInfo.getColumns();
        Map<String, Integer> nameIndex = CollectionUtils.indexCollection(allColumns, Column::getName);
        List<Integer> userInsertColIndex = new ArrayList<>(insertStatement.getColumns().size());
        for (SQLExpr column : insertStatement.getColumns()) {
            String columnName = column.toString();
            Integer index = nameIndex.get(columnName);
            if (index == null) {
                throw new MySQLException(String.format("列%s在表中不存在", columnName));
            }
            userInsertColIndex.add(index);
        }
        this.insertData = new ArrayList<>(insertStatement.getValuesList().size());
        for (SQLInsertStatement.ValuesClause valuesClause : insertStatement.getValuesList()) {
            Cell<?>[] rowCell = new Cell<?>[allColumns.size()];
            List<SQLExpr> rowValues = valuesClause.getValues();
            for (int i = 0; i < rowValues.size(); i++) {
                SQLExpr cellExpr = rowValues.get(i);
                Integer rowIndex = userInsertColIndex.get(i);
                Cell<?> cell = SqlUtils.cellWrap(cellExpr);
                rowCell[rowIndex] = cell;
            }
            for (int i = 0; i < rowCell.length; i++) {
                if (rowCell[i] == null && allColumns.get(i).getDefaultValue() != null) {
                    rowCell[i] = allColumns.get(i).getDefaultValue().toCell();
                }
            }
            this.insertData.add(Arrays.asList(rowCell));
        }

    }

}


