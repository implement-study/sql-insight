package tech.insight.core.engine

import tech.insight.core.annotation.Temporary
import tech.insight.core.bean.Index
import tech.insight.core.command.Command
import tech.insight.core.command.CreateDatabase
import tech.insight.core.command.CreateTable
import tech.insight.core.command.DeleteCommand
import tech.insight.core.command.DropDatabase
import tech.insight.core.command.DropTable
import tech.insight.core.command.InsertCommand
import tech.insight.core.command.SelectCommand
import tech.insight.core.command.UnknownCommand
import tech.insight.core.command.UpdateCommand
import tech.insight.core.command.UseDatabaseCommand
import tech.insight.core.plan.CreateDatabasePlan
import tech.insight.core.plan.CreateTablePlan
import tech.insight.core.plan.DeletePlan
import tech.insight.core.plan.DropDatabasePlan
import tech.insight.core.plan.DropTablePlan
import tech.insight.core.plan.ExecutionPlan
import tech.insight.core.plan.InsertPlan
import tech.insight.core.plan.SelectPlan
import tech.insight.core.plan.UpdatePlan
import tech.insight.core.plan.UseDatabasePlan


/**
 * like mysql query optimizer.
 * but implementation is so difficult.
 * perhaps this optimizer only a very simple function
 *
 * @author gongxuanzhangmelt@gmail.com
 */
fun interface Optimizer {

    /**
     * make a plan
     *
     * @param command from sql
     * @return execute plan
     */
    fun optimize(command: Command): ExecutionPlan
}


/**
 * the default implement optimizer
 */
object OptimizerImpl : Optimizer {


    @Temporary("how to optimizer?")
    override fun optimize(command: Command): ExecutionPlan {
        return when (command) {
            is CreateDatabase -> CreateDatabasePlan(command)
            is CreateTable -> CreateTablePlan(command)
            is DropDatabase -> DropDatabasePlan(command)
            is DropTable -> DropTablePlan(command)
            is DeleteCommand -> DeletePlan(command)
            is InsertCommand -> InsertPlan(command)
            is SelectCommand -> optimizeSelect(command)
            is UpdateCommand -> UpdatePlan(command)
            is UseDatabaseCommand -> UseDatabasePlan(command)
            is UnknownCommand -> command.unsupported()
        }

    }


    private fun optimizeSelect(selectCommand: SelectCommand): SelectPlan {
        val engine = selectCommand.table.engine
        engine.openTable(selectCommand.table)
        return SelectPlan(selectCommand, selectIndex(selectCommand))
    }

    private fun selectIndex(selectCommand: SelectCommand): Index {
        val indexList = selectCommand.table.indexList
        if (indexList.size == 1) {
            return indexList[0]
        }
        TODO()
    }


}




