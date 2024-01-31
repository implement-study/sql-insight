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

package tech.insight.core.event

import tech.insight.core.extension.slf4j
import java.lang.reflect.ParameterizedType

/**
 * @author gongxuanzhangmelt@gmail.com
 */
object EventPublisher {
    private val log = slf4j<EventPublisher>()
    private val listenerMap: MutableMap<Class<out InsightEvent>, MutableList<EventListener<InsightEvent>>> = HashMap()

    fun publishEvent(event: InsightEvent) {
        listenerMap[event.javaClass]?.forEach { it.onEvent(event) }
    }

    fun registerMultipleListener(listener: MultipleEventListener) {
        for (type in listener.listenEvent()) {
            registerListener(type) { e -> listener.onEvent(e) }
        }
    }


    fun registerListener(listener: EventListener<in InsightEvent>) {
        val eventType = getEventType(listener)
        registerListener(eventType, listener)
    }

    private fun <E : InsightEvent> getEventType(listener: EventListener<E>): Class<out InsightEvent> {
        val typeArguments = (listener::class.java.genericInterfaces.first() as ParameterizedType).actualTypeArguments
        if (typeArguments.isNotEmpty()) {
            return typeArguments[0] as Class<out InsightEvent>
        } else {
            throw IllegalArgumentException("EventListener does not have type arguments.")
        }
    }

    private fun registerListener(type: Class<out InsightEvent>, listener: EventListener<InsightEvent>) {
        log.info("register listener {} listen {}", listener.javaClass.getName(), type.getName())
        listenerMap.computeIfAbsent(type) { ArrayList() }.add(listener)
    }

}
