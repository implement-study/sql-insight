package tech.insight.core.engine

import tech.insight.core.command.*
import tech.insight.core.optimizer.CreateDatabasePlan
import tech.insight.core.optimizer.CreateTablePlan
import tech.insight.core.optimizer.ExecutionPlan


/**
 * like mysql query optimizer.
 * but implementation is so difficult.
 * perhaps this optimizer only a very simple function
 *
 * @author gongxuanzhangmelt@gmail.com
 */
interface Optimizer {

    /**
     * make a plan
     *
     * @param command from sql
     * @return execute plan
     */
    fun assign(command: Command): ExecutionPlan
}


/**
 * the default implement optimizer
 */
object OptimizerImpl : Optimizer {

    override fun assign(command: Command): ExecutionPlan {
        return when (command) {
            is CreateDatabase -> CreateDatabasePlan(command)
            is CreateTable -> CreateTablePlan(command)
            is DropDatabase -> TODO()
            is DropTable -> TODO()
            is DeleteCommand -> TODO()
            is InsertCommand -> TODO()
            is SelectCommand -> TODO()
            is UpdateCommand -> TODO()
            else -> throw UnsupportedOperationException("dont support explain " + command.sql)
        }

    }

}



