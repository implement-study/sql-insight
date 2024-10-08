package tech.insight.core.plan

import java.util.concurrent.TimeUnit
import tech.insight.core.bean.Index
import tech.insight.core.bean.Row
import tech.insight.core.bean.Where
import tech.insight.core.command.SelectCommand
import tech.insight.core.engine.storage.StorageEngine
import tech.insight.core.logging.TimeReport
import tech.insight.core.result.ResultInterface
import tech.insight.core.result.SelectResult


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class SelectPlan(
    private val command: SelectCommand,
    private val assignIndex: Index,
    private val explainType: ExplainType
) : WhereExecutionPlan(command) {

    override val engine: StorageEngine = command.table.engine

    override fun run(): ResultInterface {
        val cursor = engine.cursor(assignIndex, command.queryCondition.where, explainType)
        var skipped = 0
        val rows = arrayListOf<Row>()
        val limit = command.queryCondition.limit
        val where = where()

        TimeReport.timeReport("scan", TimeUnit.NANOSECONDS) {
            //   todo where cover the index?
            while (rows.size < limit.rowCount && cursor.hasNext()) {
                val next: Row = cursor.next()
                if (where.getBooleanValue(next)) {
                    if (skipped != limit.offset) {
                        skipped++
                        continue
                    }
                    rows.add(next)
                }
            }
            cursor.close()
        }

        command.queryCondition.orderBy?.run { rows.sortWith(this) }
        return SelectResult(rows)
    }

    override fun where(): Where {
        return command.queryCondition.where
    }

    override fun toString(): String {
        return "Select plan: ${command.sql}"
    }
}

