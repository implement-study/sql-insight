package tech.insight.core.engine

import tech.insight.core.command.*
import tech.insight.core.plan.*


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
            is DropDatabase -> DropDatabasePlan(command)
            is DropTable -> DropTablePlan(command)
            is DeleteCommand -> DeletePlan(command)
            is InsertCommand -> InsertPlan(command)
            is SelectCommand -> TODO()
            is UpdateCommand -> UpdatePlan(command)
            else -> throw UnsupportedOperationException("dont support explain " + command.sql)
        }

    }

}



