package tech.insight.core.plan

import tech.insight.core.bean.Table
import tech.insight.core.command.InsertCommand
import tech.insight.core.engine.storage.StorageEngine
import tech.insight.core.result.MessageResult
import tech.insight.core.result.ResultInterface


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class InsertPlan(private val command: InsertCommand) : DMLExecutionPlan(command) {

    override val engine: StorageEngine = command.table.engine

    private val table: Table = command.table

    override fun run(): ResultInterface {
        engine.openTable(table)
        command.insertRows.forEach { engine.insertRow(it) }
        engine.refresh(table)
        return MessageResult("insert ${command.insertRows.size} rows")
    }

    override fun toString(): String {
        return "Insert[${command.insertRows.size} rows]"
    }

}

