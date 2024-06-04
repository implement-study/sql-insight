package tech.insight.core.engine

import tech.insight.core.annotation.Temporary
import tech.insight.core.bean.Index
import tech.insight.core.command.*
import tech.insight.core.exception.UnknownColumnException
import tech.insight.core.plan.*

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
        if (errorIdentifier != null) {
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
