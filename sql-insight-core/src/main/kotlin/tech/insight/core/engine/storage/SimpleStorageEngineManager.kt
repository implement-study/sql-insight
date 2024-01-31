/*
 * Copyright 2023 java-mysql  and the original author or authors <gongxuanzhangmelt@gmail.com>.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/implement-study/sql-insight/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("UNCHECKED_CAST")

package tech.insight.core.engine.storage

import tech.insight.core.environment.DefaultProperty
import tech.insight.core.environment.GlobalContext
import tech.insight.core.environment.StorageEngineManager
import tech.insight.core.event.EventListener
import tech.insight.core.event.EventPublisher
import tech.insight.core.event.InsightEvent
import tech.insight.core.event.MultipleEventListener
import tech.insight.core.exception.DuplicationEngineNameException
import tech.insight.core.exception.EngineNotFoundException
import tech.insight.core.extension.slf4j
import java.util.*
import java.util.concurrent.ConcurrentHashMap


/**
 * @author gongxuanzhangmelt@gmail.com
 */
object EngineManager : StorageEngineManager {
    private val storageEngineMap: MutableMap<String, StorageEngine> = ConcurrentHashMap()
    private val log = slf4j<EngineManager>()

    override fun allEngine(): List<StorageEngine> {
        return ArrayList(storageEngineMap.values)
    }

    override fun registerEngine(engine: StorageEngine) {
        log.info("register engine [{}], class {}", engine.name, engine.javaClass.getName())
        if (storageEngineMap.putIfAbsent(engine.name.uppercase(Locale.getDefault()), engine) != null) {
            throw DuplicationEngineNameException("engine ${engine.name} already register ")
        }
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
