package tech.insight.core.engine

import tech.insight.core.command.Command
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
//        if (command.directly()) {
//            return DirectlyExecutionPlan(command)
//        }
//        if (command is DmlCommand) {
//            return command.plan()
//        }
        throw UnsupportedOperationException("dont support explain " + command.sql)
    }

}



