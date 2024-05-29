package tech.insight.core.environment

import tech.insight.core.bean.Database
import tech.insight.core.plan.ExecutionPlan


/**
 * session
 * @author gongxuanzhangmelt@gmail.com
 *
 */
class Session(var id: Long) {

    var database: Database? = null

    var currentPlan: ExecutionPlan? = null

    val startTime: Long = System.currentTimeMillis()

    fun runtimeMill(): Long {
        return System.currentTimeMillis() - startTime
    }

    fun close() {
        SessionManager.closeSession(this)
    }

}
