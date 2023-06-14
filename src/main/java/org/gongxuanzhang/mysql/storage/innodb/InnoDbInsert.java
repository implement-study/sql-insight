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

package org.gongxuanzhang.mysql.storage.innodb;

import org.gongxuanzhang.mysql.core.InnoDbPageSelector;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.Cell;
import org.gongxuanzhang.mysql.entity.Column;
import org.gongxuanzhang.mysql.entity.InsertInfo;
import org.gongxuanzhang.mysql.entity.InsertRow;
import org.gongxuanzhang.mysql.entity.page.InnoDbPage;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.storage.InsertEngine;

import java.util.List;

/**
 * innodb 引擎的插入模板
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class InnoDbInsert implements InsertEngine {


    @Override
    public Result insert(InsertInfo info) throws MySQLException {
        List<Column> columns = info.getTableInfo().getColumns();
        InnoDbPageSelector selector = InnoDbPageSelector.open(info.getTableInfo());
        for (InsertRow row : info.getInsertData()) {
            checkAndSwapRow(row, columns);
            doInsert(row, selector);
        }
        return Result.info("成功插入" + info.getInsertData().size() + "条数据");
    }

    private void doInsert(InsertRow row, InnoDbPageSelector selector) throws MySQLException {
        //  选择需要插入到哪个页,如果没有就得创建
        InnoDbPage targetPage = selector.selectPage(row);
        targetPage.insert(row);
    }


    private void checkAndSwapRow(InsertRow row, List<Column> columns) throws MySQLException {
        List<Cell<?>> cellList = row.getCellList();
        //  columns 和cell的数量一定是一样的
        for (int i = 0; i < cellList.size(); i++) {
            Cell<?> cell = cellList.get(i);
            columns.get(i).check(cell);
        }
    }


}
