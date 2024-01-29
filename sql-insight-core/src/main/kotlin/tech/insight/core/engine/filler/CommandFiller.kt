package tech.insight.core.engine.filler

import com.alibaba.druid.sql.ast.statement.SQLCreateDatabaseStatement
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement
import com.alibaba.druid.sql.ast.statement.SQLDropDatabaseStatement
import tech.insight.core.bean.Table
import tech.insight.core.command.*


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
    val table = Table()

    override fun fill(command: CreateTable) {
        super.fill(command)
        command.table = this.table
    }

    override fun endVisit(x: SQLCreateTableStatement) {
        command.ifNotExists = x.isIfNotExists
        x.accept(TableFiller(table))
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

class InsertFiller : CommandFiller<InsertCommand> {

}

class SelectFiller : CommandFiller<SelectCommand> {

}

class UpdateFiller : CommandFiller<UpdateCommand> {

}
