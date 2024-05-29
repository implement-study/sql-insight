package tech.insight.core.plan

import tech.insight.core.command.DeleteCommand
import tech.insight.core.result.DeleteResult
import tech.insight.core.result.ResultInterface


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class DeletePlan(private val command: DeleteCommand) : DMLExecutionPlan(command) {

    val table = command.table

    override val engine = table.engine

    override fun run(): ResultInterface {
        TODO()
//        engine.openTable(table)
//        val index = table.indexList[0]
//        index.rndInit()
//        val cursor = index.cursor()
//        var deleteCount = 0
//        while (cursor.hasNext()) {
//            val row = cursor.next()
//            if (command.where.getBooleanValue(row)) {
//                engine.delete(row)
//                deleteCount++
//            }
//        }
//        engine.refresh(table)
//        return DeleteResult(deleteCount, table)
    }

}

