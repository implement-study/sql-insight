@file:Suppress("UNCHECKED_CAST")

package tech.insight.core.environment

import java.util.*
import java.util.concurrent.ConcurrentHashMap
import tech.insight.core.engine.storage.StorageEngine
import tech.insight.core.event.EventListener
import tech.insight.core.event.EventPublisher
import tech.insight.core.event.InsightEvent
import tech.insight.core.event.MultipleEventListener
import tech.insight.core.exception.DuplicationEngineNameException
import tech.insight.core.exception.EngineNotFoundException
import tech.insight.core.logging.Logging


/**
 * engine manager
 * @author gongxuanzhangmelt@gmail.com
 */
object EngineManager : Logging(), StorageEngineManager {
    private val storageEngineMap: MutableMap<String, StorageEngine> = ConcurrentHashMap()

    init {
        EngineLoader.loadEngine().forEach { registerEngine(it) }
    }

    override fun allEngine(): List<StorageEngine> {
        return ArrayList(storageEngineMap.values)
    }

    override fun registerEngine(engine: StorageEngine) {
        info("register engine [${engine.name}], class ${engine.javaClass.getName()}")
        if (storageEngineMap.putIfAbsent(engine.name.uppercase(Locale.getDefault()), engine) != null) {
            throw DuplicationEngineNameException("engine ${engine.name} already register ")
        }
        engine.initEngine()
        if (engine is MultipleEventListener) {
            EventPublisher.registerMultipleListener(engine)
        } else if (engine is EventListener<*>) {
            EventPublisher.registerListener(engine as EventListener<in InsightEvent>)
        }
    }


    override fun selectEngine(engineName: String?): StorageEngine {
        val finalEngineName = engineName ?: GlobalContext[DefaultProperty.DEFAULT_ENGINE]
        return storageEngineMap[finalEngineName.uppercase(Locale.getDefault())] ?: throw EngineNotFoundException(
            finalEngineName
        )
    }
}
