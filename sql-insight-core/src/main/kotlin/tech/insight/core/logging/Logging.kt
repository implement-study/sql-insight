package tech.insight.core.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker


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
            log.debug(messageSupplier.invoke())
        }
    }

    fun info(marker: Marker?, msg: String?, t: Throwable?) {
        log.info(marker, msg, t)
    }

    fun info(marker: Marker?, format: String?, vararg arguments: Any?) {
        log.info(marker, format, arguments)
    }

    fun info(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        log.info(marker, format, arg1, arg2)
    }

    fun info(marker: Marker?, format: String?, arg: Any?) {
        log.info(marker, format, arg)
    }

    fun info(marker: Marker?, msg: String?) {
        log.info(marker, msg)
    }

    fun info(msg: String?, t: Throwable?) {
        log.info(msg, t)
    }

    fun info(format: String?, vararg arguments: Any?) {
        log.info(format, arguments)
    }

    fun info(format: String?, arg1: Any?, arg2: Any?) {
        log.info(format, arg1, arg2)
    }

    fun info(format: String?, arg: Any?) {
        log.info(format, arg)
    }

    fun info(msg: String?) {
        log.info(msg)
    }

    fun warn(marker: Marker?, msg: String?, t: Throwable?) {
        log.warn(marker, msg, t)
    }

    fun warn(marker: Marker?, format: String?, vararg arguments: Any?) {
        log.warn(marker, format, arguments)
    }

    fun warn(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        log.warn(marker, format, arg1, arg2)
    }

    fun warn(marker: Marker?, format: String?, arg: Any?) {
        log.warn(marker, format, arg)
    }

    fun warn(marker: Marker?, msg: String?) {
        log.warn(marker, msg)
    }

    fun warn(msg: String?, t: Throwable?) {
        log.warn(msg, t)
    }

    fun warn(format: String?, arg1: Any?, arg2: Any?) {
        log.warn(format, arg1, arg2)
    }

    fun warn(format: String?, vararg arguments: Any?) {
        log.warn(format, arguments)
    }

    fun warn(format: String?, arg: Any?) {
        log.warn(format, arg)
    }

    fun warn(msg: String?) {
        log.warn(msg)
    }

    fun error(msg: String?) {
        log.error(msg)
    }

    fun error(format: String?, arg: Any?) {
        log.error(format, arg)
    }

    fun error(format: String?, arg1: Any?, arg2: Any?) {
        log.error(format, arg1, arg2)
    }

    fun error(format: String?, vararg arguments: Any?) {
        log.error(format, arguments)
    }

    fun error(msg: String?, t: Throwable?) {
        log.error(msg, t)
    }

    fun error(marker: Marker?, msg: String?) {
        log.error(marker, msg)
    }

    fun error(marker: Marker?, format: String?, arg: Any?) {
        log.error(marker, format, arg)
    }

    fun error(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        log.error(marker, format, arg1, arg2)
    }

    fun error(marker: Marker?, format: String?, vararg arguments: Any?) {
        log.error(marker, format, arguments)
    }

    fun error(marker: Marker?, msg: String?, t: Throwable?) {
        log.error(marker, msg, t)
    }


}
