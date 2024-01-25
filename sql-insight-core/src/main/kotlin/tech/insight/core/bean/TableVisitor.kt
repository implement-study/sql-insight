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
package tech.insight.core.bean

import com.alibaba.druid.sql.ast.SQLObject
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr
import org.gongxuanzhang.sql.insight.core.environment.SqlInsightContext

/**
 * @author gongxuanzhangmelt@gmail.com
 */
class TableVisitor : SQLASTVisitor, TableContainer {
    override var table: Table? = null
    var databaseName: String? = null
    var tableName: String? = null
    override fun visit(x: SQLPropertyExpr): Boolean {
        databaseName = x.getOwnerName()
        tableName = x.getName()
        table = SqlInsightContext.getInstance().getTableDefinitionManager().select(databaseName, tableName)
        return false
    }

    override fun visit(x: SQLIdentifierExpr): Boolean {
        tableName = x.getName()
        table = SqlInsightContext.getInstance().getTableDefinitionManager().select(databaseName, tableName)
        return false
    }

    override fun postVisit(x: SQLObject) {
        if (table != null) {
            return
        }
        val tempTable = Table()
        tempTable.setName(tableName)
        if (databaseName != null) {
            tempTable.setDatabase(Database(databaseName!!))
        }
        throw TableNotExistsException(tempTable)
    }

    override fun getTable(): Table? {
        return table
    }

    override fun setTable(table: Table?) {
        throw UnsupportedOperationException("table visitor inner table only visa visit")
    }
}
