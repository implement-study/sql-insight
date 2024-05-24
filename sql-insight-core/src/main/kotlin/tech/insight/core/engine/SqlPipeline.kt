package tech.insight.core.engine

import tech.insight.core.environment.GlobalContext
import tech.insight.core.logging.Logging
import tech.insight.core.plan.DDLExecutionPlan
import tech.insight.core.plan.DMLExecutionPlan
import tech.insight.core.plan.ExecutionPlan
import tech.insight.core.result.ResultInterface


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

    private val optimizer: Optimizer = OptimizerImpl
    private val analyzer: Analyzer = DruidAnalyzer
    private val executeEngine: ExecuteEngine = ExecuteEngineImpl


    fun executeSql(sql: String): ResultInterface {
        val startTime = System.currentTimeMillis()
        info("start analysis sql \n $sql  ...")
        val command = analyzer.analysisSql(sql)
        info("start optimize command $command")
        return when (val plan = optimizer.assign(command)) {
            is DDLExecutionPlan -> {
                doExecutePlan(plan, startTime)
            }

            is DMLExecutionPlan -> {
                plan.engine.initSessionContext().use {
                    doExecutePlan(plan, startTime)
                }
            }

            else -> throw IllegalArgumentException("plan type error")
        }
    }

    private fun doExecutePlan(plan: ExecutionPlan, startTime: Long): ResultInterface {
        val resultInterface = executeEngine.executePlan(plan)
        val sql = plan.originalSql.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        info("end sql \n $sql ...take time ${System.currentTimeMillis() - startTime}ms")
        return resultInterface
    }
}





