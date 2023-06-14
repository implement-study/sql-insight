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

package org.gongxuanzhang.mysql.entity;

import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import lombok.Data;
import org.gongxuanzhang.mysql.core.OrderBox;
import org.gongxuanzhang.mysql.core.TableInfoBox;
import org.gongxuanzhang.mysql.core.WhereBox;
import org.gongxuanzhang.mysql.core.select.As;
import org.gongxuanzhang.mysql.core.select.Order;
import org.gongxuanzhang.mysql.core.select.Where;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.tool.TableInfoUtils;

/**
 * 单表查询信息
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class SingleSelectInfo implements ExecuteInfo, TableInfoBox, WhereBox, OrderBox {

    public SingleSelectInfo(MySqlSelectQueryBlock select) throws MySQLException {
        setTableInfo(TableInfoUtils.selectTableInfo(select.getFrom().toString()));
    }

    private TableInfo from;

    private As as;

    private Where where;

    private Order<?> order;


    @Override
    public TableInfo getTableInfo() {
        return from;
    }

    @Override
    public void setTableInfo(TableInfo tableInfo) {
        this.from = tableInfo;
    }
}
