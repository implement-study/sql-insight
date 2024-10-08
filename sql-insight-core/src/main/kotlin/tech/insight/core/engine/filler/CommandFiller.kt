package tech.insight.core.engine.filler

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition
import com.alibaba.druid.sql.ast.statement.SQLCreateDatabaseStatement
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement
import com.alibaba.druid.sql.ast.statement.SQLDropDatabaseStatement
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement
import com.alibaba.druid.sql.ast.statement.SQLExplainStatement
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement.ValuesClause
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement
import com.alibaba.druid.sql.ast.statement.SQLUseStatement
import com.alibaba.druid.sql.visitor.SQLASTVisitor
import tech.insight.core.bean.ExpressionVisitor
import tech.insight.core.bean.InsertRow
import tech.insight.core.bean.Table
import tech.insight.core.bean.desc.ColumnDesc
import tech.insight.core.bean.desc.TableDesc
import tech.insight.core.command.AlterCommand
import tech.insight.core.command.Command
import tech.insight.core.command.CreateCommand
import tech.insight.core.command.CreateDatabase
import tech.insight.core.command.CreateTable
import tech.insight.core.command.DCLCommand
import tech.insight.core.command.DDLCommand
import tech.insight.core.command.DMLCommand
import tech.insight.core.command.DeleteCommand
import tech.insight.core.command.DropCommand
import tech.insight.core.command.DropDatabase
import tech.insight.core.command.DropTable
import tech.insight.core.command.InsertCommand
import tech.insight.core.command.SelectCommand
import tech.insight.core.command.UnknownCommand
import tech.insight.core.command.UpdateCommand
import tech.insight.core.command.UseDatabaseCommand
import tech.insight.core.environment.TableManager
import tech.insight.core.exception.InsertException


interface CommandFiller<in C : Command> : Filler<C> {

    /**
     * fill field after create an empty command.
     */
    override fun fill(command: C)

}

object DispatcherFiller : CommandFiller<Command> {

    override fun fill(command: Command) {
        when (command) {
            is DDLCommand -> DDLFiller.fill(command)
            is DMLCommand -> DMLFiller.fill(command)
            is DCLCommand -> DCLFiller.fill(command)
            is UnknownCommand -> command.unsupported()
        }
    }
}

object DDLFiller : CommandFiller<DDLCommand> {
    override fun fill(command: DDLCommand) {
        when (command) {
            is CreateCommand -> CreateFiller.fill(command)
            is AlterCommand -> AlterFiller.fill(command)
            is DropCommand -> DropFiller.fill(command)
        }
    }
}

object DMLFiller : CommandFiller<DMLCommand> {
    override fun fill(command: DMLCommand) {
        when (command) {
            is DeleteCommand -> DeleteFiller().fill(command)
            is InsertCommand -> InsertFiller().fill(command)
            is SelectCommand -> SelectFiller().fill(command)
            is UpdateCommand -> UpdateFiller().fill(command)
        }
    }
}

object DCLFiller : CommandFiller<DCLCommand> {
    override fun fill(command: DCLCommand) {
        when (command) {
            is UseDatabaseCommand -> UseDatabaseFiller().fill(command)
        }
    }
}

object CreateFiller : CommandFiller<CreateCommand> {
    override fun fill(command: CreateCommand) {
        when (command) {
            is CreateDatabase -> CreateDatabaseFiller().fill(command)
            is CreateTable -> CreateTableFiller().fill(command)
        }
    }
}

object AlterFiller : CommandFiller<AlterCommand> {

    override fun fill(command: AlterCommand) {
        TODO()
    }
}

object DropFiller : CommandFiller<DropCommand> {

    override fun fill(command: DropCommand) {
        when (command) {
            is DropDatabase -> DropDatabaseFiller().fill(command)
            is DropTable -> DropTableFiller().fill(command)
        }
    }
}

abstract class BaseFiller<C : Command> : CommandFiller<C> {
    lateinit var command: C
    override fun fill(command: C) {
        this.command = command
        this.command.statement.accept(this)
    }
}


class CreateDatabaseFiller : BaseFiller<CreateDatabase>() {
    override fun endVisit(x: SQLCreateDatabaseStatement) {
        this.command.ifNotExists = x.isIfNotExists
        this.command.dbName = x.databaseName
    }
}

class CreateTableFiller : BaseFiller<CreateTable>() {

    override fun endVisit(x: SQLCreateTableStatement) {
        command.ifNotExists = x.isIfNotExists
        x.accept(TableCreateFiller { command.tableDesc = it })
    }

    inner class TableCreateFiller(private val tableAction: (TableDesc) -> Unit) : BeanFiller<Table> {
        private val tableDesc = TableDesc()

        override fun visit(x: SQLColumnDefinition): Boolean {
            val columnDesc = ColumnDesc()
            x.accept(ColumnFiller(columnDesc))
            tableDesc.columnList.add(columnDesc)
            return true
        }


        override fun visit(x: SQLCreateTableStatement): Boolean {
            x.comment?.accept(CommentVisitor { tableDesc.comment = it })
            x.engine?.accept(EngineVisitor { tableDesc.engine = it })
            return true
        }

        override fun endVisit(x: SQLCreateTableStatement?) {
            tableAction.invoke(tableDesc)
        }

        override fun visit(x: SQLExprTableSource): Boolean {
            x.accept(TableNameVisitor { databaseName, tableName ->
                tableDesc.databaseName = databaseName
                tableDesc.name = tableName
            })
            return true
        }

    }
}

class DropDatabaseFiller : BaseFiller<DropDatabase>() {

    override fun endVisit(x: SQLDropDatabaseStatement) {
        command.ifIsExists = x.isIfExists
        command.databaseName = x.databaseName
    }
}

class DropTableFiller : BaseFiller<DropTable>() {

    override fun endVisit(x: SQLDropTableStatement) {
        command.ifExists = x.isIfExists
        with(command.dropTables) {
            x.tableSources.forEach {
                it.accept(TableSelectVisitor(!command.ifExists) { table ->
                    if (table != null) {
                        this.add(table)
                    }
                })
            }
        }
    }
}

open class ExplainableFiller<D : DMLCommand> : BaseFiller<D>() {

    override fun endVisit(x: SQLExplainStatement) {
        command.isExplain = x.isExtended
    }
}

class DeleteFiller : ExplainableFiller<DeleteCommand>() {
    override fun visit(x: SQLDeleteStatement): Boolean {
        x.where.accept(WhereVisitor {
            command.where = it
        })
        x.tableSource.accept(TableSelectVisitor(true) {
            command.table = it!!
            command.where.table = it
        })
        return false
    }

}

class InsertFiller : ExplainableFiller<InsertCommand>() {

    override fun visit(x: SQLInsertStatement): Boolean {
        val columnVisitor = ColumnVisitor()
        //  table source accept must before column accept
        x.tableSource.accept(TableNameVisitor { databaseName, tableName ->
            command.table = TableManager.require(databaseName, tableName)
        })
        x.columns.forEach { it.accept(columnVisitor) }
        val valueVisitor = ValuesClauseVisitor()
        x.valuesList.forEach { it.accept(valueVisitor) }
        return false
    }

    /**
     * visit values clause must after visit table because insert row should have complete table info before visit
     * values clause
     */
    inner class ValuesClauseVisitor : SQLASTVisitor {
        private var rowIndex = 1L
        override fun endVisit(x: ValuesClause) {
            if (x.values.size != command.insertColumns.size) {
                throw InsertException(rowIndex, "Column count doesn't match value count")
            }
            val row = InsertRow(rowIndex++, command.table)
            command.insertRows.add(row)
            x.accept(InsertRowFiller(command.insertColumns, row))
        }
    }


    inner class ColumnVisitor : SQLASTVisitor {
        private val rowNameSet: MutableSet<String> = HashSet()
        override fun endVisit(x: SQLIdentifierExpr) {
            val colName = x.name
            if (!rowNameSet.add(colName)) {
                throw InsertException("Column $colName specified twice")
            }
            command.insertColumns.add(command.table.getColumnByName(colName))
        }
    }

}


class UpdateFiller : ExplainableFiller<UpdateCommand>() {

    override fun visit(x: SQLUpdateStatement): Boolean {
        x.tableSource.accept(TableSelectVisitor(true) { command.table = it!! })
        x.where?.accept(WhereVisitor {
            command.where = it
        })
        x.items.forEach { it.accept(this) }
        return false
    }


    override fun visit(x: SQLUpdateSetItem): Boolean {
        val column = x.column.toString()
        //  check column name
        command.table.getColumnByName(column)
        x.value.accept(ExpressionVisitor { command.updateField[column] = it })
        return false
    }
}

class UseDatabaseFiller : BaseFiller<UseDatabaseCommand>() {

    override fun fill(command: UseDatabaseCommand) {
        command.databaseName = (command.statement as SQLUseStatement).database.toString()
    }

}
