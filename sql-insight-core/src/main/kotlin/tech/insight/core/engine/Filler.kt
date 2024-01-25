package tech.insight.core.engine

import com.alibaba.druid.sql.visitor.SQLASTVisitor
import tech.insight.core.command.*


/**
 * this file have some class that fill empty command field
 * @author gongxuanzhangmelt@gmail.com
 */

interface Filler<in C : Command> : SQLASTVisitor {
    fun fill(command: C) {
        command.statement.accept(this)
    }
}


object DispatcherFiller : Filler<Command> {

    override fun fill(command: Command) {
        when (command) {
            is DDLCommand -> DDLFiller.fill(command)
            is DMLCommand -> DMLFiller.fill(command)
        }
    }
}

object DDLFiller : Filler<DDLCommand> {
    override fun fill(command: DDLCommand) {
        when (command) {
            is CreateCommand -> CreateFiller.fill(command)
            is AlterCommand -> AlterFiller.fill(command)
            is DropCommand -> DropFiller.fill(command)
        }
    }
}

object DMLFiller : Filler<DMLCommand> {
    override fun fill(command: DMLCommand) {
        when (command) {
            is DeleteCommand -> DeleteFiller().fill(command)
            is InsertCommand -> InsertFiller().fill(command)
            is SelectCommand -> SelectFiller().fill(command)
            is UpdateCommand -> UpdateFiller().fill(command)
        }
    }
}

object CreateFiller : Filler<CreateCommand> {
    override fun fill(command: CreateCommand) {
    }
}

object AlterFiller : Filler<AlterCommand> {
    override fun fill(command: AlterCommand) {
    }
}

object DropFiller : Filler<DropCommand> {
    override fun fill(command: DropCommand) {
    }
}

class DeleteFiller : Filler<DeleteCommand> {

}

class InsertFiller : Filler<InsertCommand> {

}

class SelectFiller : Filler<SelectCommand> {

}

class UpdateFiller : Filler<UpdateCommand> {

}

