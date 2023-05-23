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
import org.gongxuanzhang.mysql.tool.MapUtils;
import org.gongxuanzhang.mysql.tool.SqlUtils;
import org.gongxuanzhang.mysql.tool.TableInfoUtils;

import java.util.ArrayList;
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
    private List<InsertRow> insertData;


    public InsertInfo(MySqlInsertStatement insertStatement) throws MySQLException {
        this.tableInfo = TableInfoUtils.selectTableInfo(insertStatement.getTableName().toString());
        fillInsertColumns(insertStatement);
    }

    private void fillInsertColumns(MySqlInsertStatement insertStatement) throws MySQLException {
        Map<Integer, Integer> tableUserIndexMap = indexColumn(insertStatement);
        insertData = new ArrayList<>(insertStatement.getValuesList().size());
        for (SQLInsertStatement.ValuesClause valuesClause : insertStatement.getValuesList()) {
            List<Cell<?>> rowCellList = new ArrayList<>(this.tableInfo.getColumns().size());
            List<SQLExpr> rowValues = valuesClause.getValues();
            for (int i = 0; i < this.tableInfo.getColumns().size(); i++) {
                Integer userIndex = tableUserIndexMap.get(i);
                Cell<?> currentCell = userIndex == null ? new NullCell() : SqlUtils.cellWrap(rowValues.get(userIndex));
                rowCellList.add(currentCell);
            }
            insertData.add(new InsertRowImpl(rowCellList, this.tableInfo));
        }
    }


    /**
     * 把用户输入列和表的列做索引
     *
     * @return key: 表中的 col index
     * value: 用户输入的 col index
     **/
    private Map<Integer, Integer> indexColumn(MySqlInsertStatement insertStatement) throws MySQLException {
        List<Column> allColumns = tableInfo.getColumns();
        Map<Integer, Integer> result = MapUtils.newHashMapWithExpectSize(allColumns.size());
        Map<String, Integer> tableNameColIndex = CollectionUtils.indexCollection(allColumns, Column::getName);
        for (int i = 0; i < insertStatement.getColumns().size(); i++) {
            String columnName = insertStatement.getColumns().get(i).toString();
            Integer index = tableNameColIndex.get(columnName);
            if (index == null) {
                throw new MySQLException(String.format("列%s在表中不存在", columnName));
            }
            result.put(index, i);
        }
        return result;
    }

}


