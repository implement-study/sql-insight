package tech.insight.core.plan

import tech.insight.core.command.DeleteCommand
import tech.insight.core.environment.SessionManager
import tech.insight.core.result.DeleteResult
import tech.insight.core.result.ResultInterface


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class DeletePlan(private val command: DeleteCommand) : DMLExecutionPlan(command) {
    val table = command.table
    val engine = table.engine

    override val originalSql: String
        get() = command.sql

    override fun run(): ResultInterface {
        engine.openTable(table)
        //  todo select the engine
        val index = table.indexList[0]
        index.rndInit()
        val cursor = index.find(SessionManager.currentSession())
        var deleteCount = 0
        while (cursor.hasNext()) {
            val row = cursor.next()
            if (command.where.getBooleanValue(row)) {
                engine.delete(row)
                deleteCount++
            }
        }
        engine.refresh(table)
        return DeleteResult(deleteCount, table)
    }

}

