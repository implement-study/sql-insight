package tech.insight.core.extension

import com.google.common.collect.Table
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 * this file have global function
 * @author gongxuanzhangmelt@gmail.com
 */


/**
 * global function for get slf4j object
 */
inline fun <reified C> slf4j(): Logger = LoggerFactory.getLogger(C::class.java)


object TimeReport

inline fun timeReport(actionName: String, action: () -> Unit) {
    val log = slf4j<TimeReport>()
    val startTime = System.currentTimeMillis()
    log.info("run $actionName")
    action.invoke()
    log.info("finish $actionName for ${System.currentTimeMillis() - startTime} mill")
}


typealias GuavaTable<A, B, C> = Table<A, B, C>

