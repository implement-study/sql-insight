package tech.insight.core.plan

import tech.insight.core.bean.Row
import tech.insight.core.bean.Table
import tech.insight.core.command.UpdateCommand
import tech.insight.core.engine.storage.StorageEngine
import tech.insight.core.result.ResultInterface
import tech.insight.core.result.UpdateResult


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class UpdatePlan(private val command: UpdateCommand, private val explainType: ExplainType) : DMLExecutionPlan(command) {

    override val engine: StorageEngine = command.table.engine

    private val table: Table = command.table

    override fun run(): ResultInterface {
        engine.openTable(table)
        var updateCount = 0
        table.indexList.forEach {
            it.rndInit()
            val cursor = engine.cursor(it, command.where, explainType)
            val next: Row = cursor.next()
            if (command.where.getBooleanValue(next)) {
                //   todo
                engine.update(next, command)
                updateCount++
            }
        }
        engine.refresh(table)
        return UpdateResult(updateCount, table)
    }

}

