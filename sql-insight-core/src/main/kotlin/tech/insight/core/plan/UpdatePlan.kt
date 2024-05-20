package tech.insight.core.plan

import tech.insight.core.bean.Table
import tech.insight.core.command.UpdateCommand
import tech.insight.core.engine.storage.StorageEngine
import tech.insight.core.environment.SessionManager
import tech.insight.core.result.ResultInterface
import tech.insight.core.result.UpdateResult


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class UpdatePlan(private val command: UpdateCommand) : DMLExecutionPlan(command) {

    override val engine: StorageEngine = command.table.engine

    private val table: Table = command.table

    override val originalSql: String
        get() = command.sql

    override fun run(): ResultInterface {
        engine.openTable(table)
        //  todo select the engine
        val index = table.indexList[0]
        index.rndInit()
        val cursor = index.find(SessionManager.currentSession())
        var updateCount = 0
        while (cursor.hasNext()) {
            val row = cursor.next()
            if (command.where.getBooleanValue(row)) {
                engine.update(row, command)
                updateCount++
            }
        }
        engine.refresh(table)
        return UpdateResult(updateCount, table)
    }

}

