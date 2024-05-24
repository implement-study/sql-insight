/*
 * Copyright 2023 sql-insight  and the original author or authors <gongxuanzhangmelt@gmail.com>.
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

import tech.insight.core.environment.DatabaseManager
import tech.insight.core.environment.TableManager
import tech.insight.core.logging.Logging
import java.lang.reflect.ParameterizedType

/**
 * @author gongxuanzhangmelt@gmail.com
 */
object EventPublisher : Logging() {
    private val listenerMap: MutableMap<Class<out InsightEvent>, MutableList<EventListener<InsightEvent>>> = HashMap()

    init {
        this.registerMultipleListener(DatabaseManager)
        this.registerMultipleListener(TableManager)
    }

    fun publishEvent(eventSupplier: () -> InsightEvent) {
        val event = eventSupplier.invoke()
        listenerMap[event.javaClass]?.forEach { it.onEvent(event) }
    }

    fun registerMultipleListener(listener: MultipleEventListener) {
        info("register MultipleEventListener ${listener.javaClass.name}")
        for (type in listener.listenEvent()) {
            registerListener(type, true) { e -> listener.onEvent(e) }
        }
    }


    fun registerListener(listener: EventListener<in InsightEvent>) {
        val eventType = getEventType(listener)
        registerListener(eventType, listener = listener)
    }

    private fun <E : InsightEvent> getEventType(listener: EventListener<E>): Class<out InsightEvent> {
        val typeArguments = (listener::class.java.genericInterfaces.first() as ParameterizedType).actualTypeArguments
        if (typeArguments.isNotEmpty()) {
            return typeArguments[0] as Class<out InsightEvent>
        } else {
            throw IllegalArgumentException("EventListener does not have type arguments.")
        }
    }

    private fun registerListener(
        type: Class<out InsightEvent>,
        suppress: Boolean = false,
        listener: EventListener<InsightEvent>
    ) {
        if (!suppress) {
            info("register listener ${listener.javaClass.getName()} listen ${type.getName()}")
        }
        listenerMap.computeIfAbsent(type) { ArrayList() }.add(listener)
    }

}
