package tech.insight.core.plan

import tech.insight.core.bean.Where
import tech.insight.core.command.DCLCommand
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


abstract class DCLExecutionPlan(dclCommand: DCLCommand) : ExecutionPlan {

    override val originalSql: String = dclCommand.sql

}

abstract class DDLExecutionPlan(ddlCommand: DDLCommand) : ExecutionPlan {

    override val originalSql: String = ddlCommand.sql
}

abstract class DMLExecutionPlan(dmlCommand: DMLCommand) : ExecutionPlan {

    lateinit var explain: PlanDetail

    abstract val engine: StorageEngine

    override val originalSql: String = dmlCommand.sql
}

abstract class WhereExecutionPlan(dmlCommand: DMLCommand) : DMLExecutionPlan(dmlCommand) {

    /**
     * query or delete update condition
     */
    abstract fun where(): Where

}
