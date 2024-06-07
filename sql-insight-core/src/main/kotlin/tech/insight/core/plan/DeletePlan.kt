package tech.insight.core.plan

import tech.insight.core.annotation.Temporary
import tech.insight.core.bean.Row
import tech.insight.core.command.DeleteCommand
import tech.insight.core.result.DeleteResult
import tech.insight.core.result.ResultInterface


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class DeletePlan(private val command: DeleteCommand, private val explainType: ExplainType) : DMLExecutionPlan(command) {

    val table = command.table

    override val engine = table.engine


    @Temporary("delete count ")
    override fun run(): ResultInterface {
        var deleteCount = 0
        table.indexList.forEach {
            var indexDeleteCount = 0
            val cursor = engine.cursor(it, command.where, explainType)
            while (cursor.hasNext()) {
                val next: Row = cursor.next()
                if (command.where.getBooleanValue(next)) {
                    engine.delete(next)
                    indexDeleteCount++
                }
            }
            deleteCount = indexDeleteCount
        }
        engine.refresh(table)
        return DeleteResult(deleteCount, table)
    }

}

