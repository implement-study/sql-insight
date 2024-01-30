package tech.insight.core.engine.filler

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr
import com.alibaba.druid.sql.ast.statement.*
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement.ValuesClause
import com.alibaba.druid.sql.visitor.SQLASTVisitor
import tech.insight.core.bean.InsertRow
import tech.insight.core.bean.Table
import tech.insight.core.command.*
import tech.insight.core.exception.InsertException


interface CommandFiller<in C : Command> : Filler<C> {

    /**
     * fill field after create a empty command.
     */
    override fun fill(command: C)

}

object DispatcherFiller : CommandFiller<Command> {

    override fun fill(command: Command) {
        when (command) {
            is DDLCommand -> DDLFiller.fill(command)
            is DMLCommand -> DMLFiller.fill(command)
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
    override fun fill(command: CreateTable) {
        super.fill(command)
        command.table = Table()
    }

    override fun endVisit(x: SQLCreateTableStatement) {
        command.ifNotExists = x.isIfNotExists
        x.accept(TableFiller(command.table))
    }
}

class DropDatabaseFiller : BaseFiller<DropDatabase>() {

    override fun endVisit(x: SQLDropDatabaseStatement) {
        command.ifIsExists = x.isIfExists
        TODO("select the database")
    }
}

class DropTableFiller : BaseFiller<DropTable>() {

    override fun endVisit(x: SQLDropDatabaseStatement) {
        command.ifExists = x.isIfExists
        TODO("select the table")
    }
}

class DeleteFiller : BaseFiller<DeleteCommand>() {
    override fun visit(x: SQLDeleteStatement): Boolean {
        TODO("select the table fill the where")
    }
}

class InsertFiller : BaseFiller<InsertCommand>() {

    lateinit var table: Table

    override fun fill(command: InsertCommand) {
        super.fill(command)
        this.table = command.table
    }

    override fun visit(x: SQLInsertStatement): Boolean {
//        x.tableSource.accept(TableFiller(command.table))
        val columnVisitor = ColumnVisitor()
        x.columns.forEach { it.accept(columnVisitor) }
        val valueVisitor = ValuesClauseVisitor()
        x.valuesList.forEach { it.accept(valueVisitor) }
        TODO("select the table")
        return true
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
            val row = InsertRow(command.insertColumns, rowIndex++)
            row.table = table
            command.insertRows.add(row)
            x.accept(InsertRowFiller(row))
        }
    }


    inner class ColumnVisitor : SQLASTVisitor {
        private val rowNameSet: MutableSet<String> = HashSet()
        override fun endVisit(x: SQLIdentifierExpr) {
            val colName = x.name
            if (!rowNameSet.add(colName)) {
                throw InsertException("Column $colName specified twice")
            }
            command.insertColumns.add(table.getColumnByName(colName))
        }
    }

}


class SelectFiller : BaseFiller<SelectCommand>() {

}

class UpdateFiller : BaseFiller<UpdateCommand>() {

}
