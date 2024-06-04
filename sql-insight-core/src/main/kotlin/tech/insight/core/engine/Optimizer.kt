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
import tech.insight.core.exception.UnknownColumnException
import tech.insight.core.plan.CreateDatabasePlan
import tech.insight.core.plan.CreateTablePlan
import tech.insight.core.plan.DeletePlan
import tech.insight.core.plan.DropDatabasePlan
import tech.insight.core.plan.DropTablePlan
import tech.insight.core.plan.ExecutionPlan
import tech.insight.core.plan.ExplainType
import tech.insight.core.plan.InsertPlan
import tech.insight.core.plan.SelectPlan
import tech.insight.core.plan.UpdatePlan
import tech.insight.core.plan.UseDatabasePlan

/**
 * like mysql query optimizer. but implementation is so difficult. perhaps this optimizer only a
 * very simple function
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
 *
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
        verifyCondition(selectCommand)
        val (index, explainType) = selectIndex(selectCommand)
        return SelectPlan(selectCommand, index, explainType)
    }

    private fun verifyCondition(selectCommand: SelectCommand) {
        val nameSet = selectCommand.table.indexList.map { it.name }.toSet()
        val errorIdentifier = selectCommand.queryCondition.where.identifiers().firstOrNull { !nameSet.contains(it) }
        if(errorIdentifier != null){
            throw UnknownColumnException(errorIdentifier)
        }

    }

    private fun selectIndex(selectCommand: SelectCommand): Pair<Index, ExplainType> {
        val leastCostObject =
            selectCommand.table.indexList.maxOf {
                IndexSelectReport(it, selectCommand.queryCondition)
            }
        return Pair(leastCostObject.index, leastCostObject.type())
    }
}
