package tech.insight.core.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 *
 * delegate log function to slf4j
 *  debug function to avoid unnecessary string concatenation
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
abstract class Logging {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    fun debug(messageSupplier: () -> String) {
        if (log.isDebugEnabled) {
            log.debug(messageSupplier.invoke(), SECOND)
        }
    }

    fun info(msg: String?) {
        log.info(msg, SECOND)
    }

    fun warn(msg: String?) {
        log.warn(msg, SECOND)
    }

    fun error(msg: String?) {
        log.error(msg, SECOND)
    }

    fun error(msg: String?, t: Throwable?) {
        log.error(msg, SECOND, t)
    }

    companion object {
        const val SECOND = "second"
    }


}
