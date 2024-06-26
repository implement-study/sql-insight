package tech.insight.core.engine

import tech.insight.core.environment.GlobalContext
import tech.insight.core.logging.Logging
import tech.insight.core.logging.TimeReport.timeReport
import tech.insight.core.plan.DMLExecutionPlan
import tech.insight.core.result.ResultInterface
import tech.insight.core.util.truncateStringIfTooLong


/**
 * sql insight core engine
 * execute a sql.
 *
 * sql ---Analyzer--->  command
 * command ---optimizer---->  execute plan
 * execute plan --execute engine---->  result
 *
 * @author gongxuanzhangmelt@gmail.com
 */


/**
 * sql lifecycle container
 */
object SqlPipeline : Logging() {
    init {
        //  static init context
        GlobalContext
    }

    val optimizer: Optimizer = OptimizerImpl
    val analyzer: Analyzer = DruidAnalyzer
    val executeEngine: ExecuteEngine = ExecuteEngineImpl


    fun executeSql(sql: String): ResultInterface {
        val command = timeReport("analysis sql ${truncateStringIfTooLong(sql)}") {
            analyzer.analysisSql(sql)
        }
        val plan = timeReport("optimize command $command") {
            optimizer.optimize(command)
        }
        return timeReport("execute plan $plan") {
            when (plan) {

                is DMLExecutionPlan -> {
                    plan.engine.initSessionContext().use {
                        executeEngine.executePlan(plan)
                    }
                }

                else -> {
                    executeEngine.executePlan(plan)
                }

            }
        }
    }

}





