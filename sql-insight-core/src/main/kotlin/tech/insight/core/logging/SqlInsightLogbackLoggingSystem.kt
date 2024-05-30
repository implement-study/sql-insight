package tech.insight.core.logging

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.joran.JoranConfigurator
import ch.qos.logback.classic.jul.LevelChangePropagator
import ch.qos.logback.classic.turbo.TurboFilter
import ch.qos.logback.core.spi.FilterReply
import java.net.URL
import java.util.logging.ConsoleHandler
import java.util.logging.LogManager
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.bridge.SLF4JBridgeHandler
import org.slf4j.helpers.SubstituteLoggerFactory


/**
 *
 * logback logging system referential from spring boot logging
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
object SqlInsightLogbackLoggingSystem {

    private const val SELF_INITIALIZATION_CONFIG = "logback.xml"

    private const val BRIDGE_HANDLER: String = "org.slf4j.bridge.SLF4JBridgeHandler"


    fun initialize() {
        getLoggerContext().reset()
        getLoggerContext().statusManager.clear()
        loadConfiguration()
    }


    fun beforeInitialize() {
        val loggerContext = getLoggerContext()
        configureJdkLoggingBridgeHandler()
        loggerContext.turboFilterList.add(DenyAllFilter)
    }

    private fun configureJdkLoggingBridgeHandler() {
        try {
            if (isBridgeJulIntoSlf4j()) {
                removeJdkLoggingBridgeHandler()
                SLF4JBridgeHandler.install()
            }
        } catch (ex: Throwable) {
            // Ignore. No java.util.logging bridge is installed.
        }
    }

    private fun isBridgeJulIntoSlf4j(): Boolean {
        return isBridgeHandlerAvailable() && isJulUsingASingleConsoleHandlerAtMost()
    }

    private fun isBridgeHandlerAvailable(): Boolean {
        try {
            Class.forName(BRIDGE_HANDLER)
            return true
        } catch (var4: Throwable) {
            return false
        }
    }

    private fun isJulUsingASingleConsoleHandlerAtMost(): Boolean {
        val rootLogger = LogManager.getLogManager().getLogger("")
        val handlers = rootLogger.handlers
        return handlers.isEmpty() || (handlers.size == 1 && handlers[0] is ConsoleHandler)
    }

    private fun stopAndReset(loggerContext: LoggerContext) {
        loggerContext.stop()
        loggerContext.reset()
        if (isBridgeHandlerInstalled()) {
            addLevelChangePropagator(loggerContext)
        }
    }

    private fun removeJdkLoggingBridgeHandler() {
        try {
            removeDefaultRootHandler()
            SLF4JBridgeHandler.uninstall()
        } catch (ex: Throwable) {
            // Ignore and continue
        }
    }

    private fun removeDefaultRootHandler() {
        try {
            val rootLogger = LogManager.getLogManager().getLogger("")
            val handlers = rootLogger.handlers
            if (handlers.size == 1 && handlers[0] is ConsoleHandler) {
                rootLogger.removeHandler(handlers[0])
            }
        } catch (ex: Throwable) {
            // Ignore and continue
        }
    }

    private fun isBridgeHandlerInstalled(): Boolean {
        val rootLogger = LogManager.getLogManager().getLogger("")
        val handlers = rootLogger.handlers
        return handlers.size == 1 && handlers[0] is SLF4JBridgeHandler
    }

    private fun addLevelChangePropagator(loggerContext: LoggerContext) {
        val levelChangePropagator = LevelChangePropagator()
        levelChangePropagator.setResetJUL(true)
        levelChangePropagator.context = loggerContext
        loggerContext.addListener(levelChangePropagator)
    }

    private fun loadConfiguration() {
        val loggerContext = getLoggerContext()
        stopAndReset(loggerContext)
        withLoggingSuppressed {
            try {
                configureByResourceUrl(loggerContext, javaClass.classLoader.getResource(SELF_INITIALIZATION_CONFIG)!!)
            } catch (ex: Exception) {
                throw IllegalStateException("Could not initialize Logback logging from $SELF_INITIALIZATION_CONFIG", ex)
            }
        }
    }


    private fun configureByResourceUrl(loggerContext: LoggerContext, url: URL) {
        if (url.path.endsWith(".xml")) {
            val configurator = JoranConfigurator()
            configurator.context = loggerContext
            configurator.doConfigure(url)
        } else {
            throw IllegalArgumentException("Unsupported file extension in '$url'. Only .xml is supported")
        }
    }

    private fun getLoggerContext(): LoggerContext {
        var factory = LoggerFactory.getILoggerFactory()
        while (factory is SubstituteLoggerFactory) {
            try {
                Thread.sleep(50)
            } catch (ex: InterruptedException) {
                Thread.currentThread().interrupt()
                throw IllegalStateException("Interrupted while waiting for non-substitute logger factory", ex)
            }
            factory = LoggerFactory.getILoggerFactory()
        }
        return (factory as LoggerContext)
    }


    private fun withLoggingSuppressed(action: () -> Unit) {
        val turboFilters = getLoggerContext().turboFilterList
        turboFilters.add(DenyAllFilter)
        try {
            action()
        } finally {
            turboFilters.remove(DenyAllFilter)
        }
    }

}

private object DenyAllFilter : TurboFilter() {
    override fun decide(
        marker: Marker,
        logger: Logger,
        level: Level,
        format: String,
        params: Array<Any>,
        t: Throwable
    ) = FilterReply.DENY
}
