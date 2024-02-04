package tech.insight.core.optimizer

import tech.insight.core.command.InsertCommand
import tech.insight.core.engine.storage.StorageEngine
import tech.insight.core.result.MessageResult
import tech.insight.core.result.ResultInterface


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class InsertPlan(private val command: InsertCommand) : DMLExecutionPlan(command) {
    private val engine: StorageEngine = command.table.engine
    override val originalSql: String
        get() = command.sql

    override fun run(): ResultInterface {
        command.insertRows.forEach { engine.insertRow(it) }
        return MessageResult("insert ${command.insertRows.size} rows")
    }

}

