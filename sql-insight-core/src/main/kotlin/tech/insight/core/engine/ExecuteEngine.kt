package tech.insight.core.engine

import tech.insight.core.extension.slf4j
import tech.insight.core.plan.ExecutionPlan
import tech.insight.core.result.ExceptionResult
import tech.insight.core.result.ResultInterface


/**
 * execute engine differ from {@link StorageEngine}
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
fun interface ExecuteEngine {

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

    private val log = slf4j<ExecuteEngine>()

    override fun executePlan(plan: ExecutionPlan): ResultInterface {
        return try {
            plan.run()
        } catch (e: Exception) {
            log.error("execute error", e)
            ExceptionResult(e)
        }
    }

}
