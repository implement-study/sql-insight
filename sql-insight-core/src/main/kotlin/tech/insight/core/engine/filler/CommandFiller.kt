package tech.insight.core.engine.filler

import tech.insight.core.command.*


interface CommandFiller<in C : Command> : Filler<C> {
    /**
     * fill field after create a empty command.
     */
    override fun fill(command: C) {
        command.statement.accept(this)
    }
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
            is CreateTable -> TODO()
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
    }
}


class CreateDatabaseFiller : CommandFiller<CreateDatabase> {

}


class DeleteFiller : CommandFiller<DeleteCommand> {

}

class InsertFiller : CommandFiller<InsertCommand> {

}

class SelectFiller : CommandFiller<SelectCommand> {

}

class UpdateFiller : CommandFiller<UpdateCommand> {

}
