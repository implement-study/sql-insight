package tech.insight.core.optimizer

import tech.insight.core.command.DDLCommand
import tech.insight.core.command.DMLCommand
import tech.insight.core.result.ResultInterface


/**
 * hand out to [StorageEngine] from [ExecuteEngine]
 * can also be executed directly from the execute engine
 *
 * @author gongxuanzhangmelt@gmail.com
 */
interface ExecutionPlan {
    val originalSql: String

    /**
     * execution
     */
    fun run(): ResultInterface
}


abstract class DDLExecutionPlan(val ddlCommand: DDLCommand) : ExecutionPlan


abstract class DMLExecutionPlan(val dmlCommand: DMLCommand) : ExecutionPlan
