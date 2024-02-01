package tech.insight.core.engine

import tech.insight.core.optimizer.ExecutionPlan
import tech.insight.core.result.ExceptionResultInterface
import tech.insight.core.result.ResultInterface


/**
 * execute engine differ from {@link StorageEngine}
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
interface ExecuteEngine {

    /**
     * execute plan
     *
     * @param plan execution plan from [Optimizer]
     * @return return storage engine result in general , if the sql is a dcl,return core executor result.
     * maybe return the error result if an error occurred during the sql
     * process
     */
    fun executePlan(plan: ExecutionPlan): ResultInterface
}


object ExecuteEngineImpl : ExecuteEngine {
    override fun executePlan(plan: ExecutionPlan): ResultInterface {
        return try {
            return plan.run()
        } catch (e: Exception) {
            return ExceptionResultInterface(e)
        }
    }

}
