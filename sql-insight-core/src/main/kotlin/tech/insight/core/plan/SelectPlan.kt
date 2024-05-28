package tech.insight.core.plan

import tech.insight.core.bean.*
import tech.insight.core.command.SelectCommand
import tech.insight.core.engine.storage.StorageEngine
import tech.insight.core.environment.SessionManager
import tech.insight.core.result.ResultInterface
import tech.insight.core.result.SelectResult


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
//   todo
class SelectPlan(private val command: SelectCommand) : DMLExecutionPlan(command) {

    override val engine: StorageEngine = command.table.engine

    private val table: Table = command.table

    override fun run(): ResultInterface {
        engine.openTable(table)
        val indexList: List<Index> = table.indexList
        //  decide index
        val main: Index = indexList[0]
        main.rndInit()
        val where = command.where
        val cursor: Cursor = main.find(SessionManager.currentSession())
        val limit = command.limit
        var skipped = 0
        var rowCount = 0
        val rows = arrayListOf<Row>()
        while (rowCount < limit.rowCount && cursor.hasNext()) {
            val next: Row = cursor.next()
            if (where.getBooleanValue(next)) {
                if (skipped != limit.offset) {
                    skipped++
                    continue
                }
                rowCount++
                rows.add(next)
            }
        }
        cursor.close()
        val orderBy: OrderBy = command.orderBy
        rows.sortWith(orderBy)
        return SelectResult(rows)
    }

}

