package tech.insight.core.logging

import ch.qos.logback.classic.pattern.ExtendedThrowableProxyConverter
import ch.qos.logback.classic.spi.IThrowableProxy
import ch.qos.logback.core.CoreConstants


/**
 * copy spring boot ExtendedWhitespaceThrowableProxyConverter
 */
class ExtendedWhitespaceThrowableProxyConverter : ExtendedThrowableProxyConverter() {

    override fun throwableProxyToString(tp: IThrowableProxy): String {
        return CoreConstants.LINE_SEPARATOR + super.throwableProxyToString(tp) + CoreConstants.LINE_SEPARATOR
    }
}
