package tech.insight.core.engine.filler

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource
import com.alibaba.druid.sql.visitor.SQLASTVisitor
import tech.insight.core.bean.Column
import tech.insight.core.bean.Table
import tech.insight.core.environment.DatabaseManager
import tech.insight.core.environment.EngineManager
import tech.insight.core.environment.SessionManager
import tech.insight.core.environment.TableManager
import tech.insight.core.exception.TableNotExistsException

/**
 * table create filler
 * @author gongxuanzhangmelt@gmail.com
 */

class TableNameVisitor(private val action: (databaseName: String, tableName: String) -> Unit) : SQLASTVisitor {
    override fun visit(x: SQLPropertyExpr): Boolean {
        action.invoke(x.ownerName, x.name)
        return false
    }

    override fun visit(x: SQLIdentifierExpr): Boolean {
        action.invoke(SessionManager.currentSession().database.name, x.name)
        return false
    }
}

/**
 * @param must must is true that table can't find throw [TableNotExistsException]
 */
class TableSelectVisitor(private val must: Boolean = false, private val action: (table: Table?) -> Unit) :
    SQLASTVisitor {
    override fun visit(x: SQLPropertyExpr): Boolean {
        action.invoke(selectTable(x.ownerName, x.name))
        return false
    }

    override fun visit(x: SQLIdentifierExpr): Boolean {
        val table = selectTable(SessionManager.currentSession().database.name, x.name)
        action.invoke(table)
        return false
    }

    private fun selectTable(databaseName: String, tableName: String): Table? {
        val table = TableManager.select(databaseName, tableName)
        if (table == null && must) {
            throw TableNotExistsException("${databaseName}.${tableName} not exists")
        }
        return table
    }
}
