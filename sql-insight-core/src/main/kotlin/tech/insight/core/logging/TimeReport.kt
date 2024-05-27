package tech.insight.core.logging

import kotlin.time.Duration.Companion.milliseconds


/**
 * this file have global function
 * @author gongxuanzhangmelt@gmail.com
 */
object TimeReport : Logging() {

    fun <T> timeReport(actionName: String, action: () -> T): T {
        val startTime = System.currentTimeMillis()
        val result = action.invoke()
        info("finish [$actionName] for ${(System.currentTimeMillis() - startTime).milliseconds}")
        return result
    }
}
