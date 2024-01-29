package tech.insight.core.engine.filler

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource
import com.alibaba.druid.sql.visitor.SQLASTVisitor
import tech.insight.core.bean.Column
import tech.insight.core.bean.DataType
import tech.insight.core.bean.Database
import tech.insight.core.bean.Table

/**
 * Table filler
 * @author gongxuanzhangmelt@gmail.com
 */
class TableFiller(val table: Table) : BeanFiller<Table> {
    override fun endVisit(x: SQLColumnDefinition) {
        val column = Column()
        x.accept(ColumnFiller(column))
        table.columnList.add(column)
        val ext = table.ext
        if (column.autoIncrement) {
            if (ext.autoColIndex != -1) {
                throw UnsupportedOperationException("only support single column autoincrement")
            }
            ext.autoColIndex = table.columnList.size - 1
            column.notNull = true
        }
        if (column.primaryKey) {
            if (ext.primaryKeyIndex != -1) {
                throw UnsupportedOperationException("only support single column primary key")
            }
            ext.primaryKeyIndex = table.columnList.size - 1
            ext.primaryKeyName = column.name
            column.notNull = true
        }
        if (column.notNull) {
            ext.notNullIndex.add(table.columnList.size - 1)
        } else {
            column.nullListIndex = ext.nullableColCount
            ext.nullableColCount++
        }
        if (column.dataType.type === DataType.Type.VARCHAR) {
            ext.variableIndex.add(table.columnList.size - 1)
        }
        ext.columnIndex[column.name] = table.columnList.size - 1
        ext.columnMap[column.name] = column
    }


    override fun visit(x: SQLCreateTableStatement): Boolean {
        x.comment?.accept(CommentVisitor { table.comment = it })
        x.engine?.accept(EngineVisitor { table.engine = it })
        return true
    }

    override fun visit(x: SQLExprTableSource): Boolean {
        x.accept(TableNameVisitor())
        return true
    }


    inner class TableNameVisitor : SQLASTVisitor {
        override fun visit(x: SQLPropertyExpr): Boolean {
            table.database = Database(x.getOwnerName())
            table.name = x.name
            return false
        }

        override fun visit(x: SQLIdentifierExpr): Boolean {
            table.name = x.name
            return false
        }
    }

}
