package tech.insight.core.plan

import tech.insight.core.command.DDLCommand
import tech.insight.core.command.DMLCommand
import tech.insight.core.engine.storage.StorageEngine
import tech.insight.core.result.ResultInterface


/**
 * sql execution plan,can use explain view details
 * @author gongxuanzhangmelt@gmail.com
 */
interface ExecutionPlan {

    val originalSql: String


    /**
     * execution
     */
    fun run(): ResultInterface

}


data class PlanDetail(val table: String, val type: ExplainType, val possibleKeys: List<String>, val key: String)

enum class ExplainType {
    ALL, INDEX, RANGE, NULL
}


abstract class DDLExecutionPlan(val ddlCommand: DDLCommand) : ExecutionPlan {

}


abstract class DMLExecutionPlan(val dmlCommand: DMLCommand) : ExecutionPlan {

    lateinit var explain: PlanDetail

    abstract val engine: StorageEngine
}
