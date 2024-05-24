package tech.insight.core.logging

import ch.qos.logback.classic.pattern.ClassicConverter
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.CoreConstants

/**
 * extend MethodOfCallerConverter
 * [Logging] support delegate logback method,but method stack is logging log method.
 * so we should
 */
class SqlInsightMethodConverter : ClassicConverter() {

    override fun convert(event: ILoggingEvent): String {
        val cda: Array<StackTraceElement> = event.callerData
        if (cda.isEmpty()) {
            return CoreConstants.NA
        }
        if (event.argumentArray.isNotEmpty() && event.argumentArray[0] == Logging.SECOND) {
            return cda[1].methodName
        }
        return cda[0].methodName
    }
}
