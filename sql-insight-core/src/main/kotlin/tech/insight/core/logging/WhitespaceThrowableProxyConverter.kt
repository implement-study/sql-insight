package tech.insight.core.logging

import ch.qos.logback.classic.pattern.ThrowableProxyConverter
import ch.qos.logback.classic.spi.IThrowableProxy
import ch.qos.logback.core.CoreConstants



/**
 * copy spring boot WhitespaceThrowableProxyConverter
 */
open class WhitespaceThrowableProxyConverter : ThrowableProxyConverter() {
    override fun throwableProxyToString(tp: IThrowableProxy): String {
        return CoreConstants.LINE_SEPARATOR + super.throwableProxyToString(tp) + CoreConstants.LINE_SEPARATOR
    }
}
