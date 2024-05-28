package tech.insight.core.plan

import tech.insight.core.command.UseDatabaseCommand
import tech.insight.core.environment.DatabaseManager
import tech.insight.core.environment.SessionManager
import tech.insight.core.result.MessageResult
import tech.insight.core.result.ResultInterface


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class UseDatabasePlan(private val useDatabaseCommand: UseDatabaseCommand) : DCLExecutionPlan(useDatabaseCommand) {

    override fun run(): ResultInterface {
        val session = SessionManager.currentSession()
        session.database = DatabaseManager.require(useDatabaseCommand.databaseName)
        return MessageResult("selected ${session.database!!.name}")
    }

    override fun toString(): String {
        return "Use Database[${useDatabaseCommand.databaseName}]"
    }
}
