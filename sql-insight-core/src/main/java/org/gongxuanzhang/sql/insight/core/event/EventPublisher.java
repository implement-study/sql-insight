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

package org.gongxuanzhang.sql.insight.core.event;


import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
@Slf4j
public class EventPublisher {

    private EventPublisher() {

    }

    private static final EventPublisher INSTANCE = new EventPublisher();

    public static EventPublisher getInstance() {
        return INSTANCE;
    }

    private final Map<Class<? extends InsightEvent>, List<EventListener<InsightEvent>>> listenerMap = new HashMap<>();


    public void registerListener(MultipleEventListener listener) {
        for (Class<? extends InsightEvent> type : listener.listenEvent()) {
            registerListener(type, listener::onEvent);
        }
    }


    public void publishEvent(InsightEvent event) {
        List<EventListener<InsightEvent>> eventListeners = this.listenerMap.get(event.getClass());
        if (eventListeners == null) {
            return;
        }
        for (EventListener<InsightEvent> eventListener : eventListeners) {
            eventListener.onEvent(event);
        }
    }


    @SuppressWarnings({"all"})
    public void registerListener(EventListener<?> listener) {
        Type[] genericInterfaces = listener.getClass().getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (!(genericInterface instanceof ParameterizedType)) {
                continue;
            }
            if (((ParameterizedType) genericInterface).getRawType() != EventListener.class) {
                continue;
            }
            Type[] actualTypeArguments = ((ParameterizedType) genericInterface).getActualTypeArguments();
            Class<? extends InsightEvent> generic = (Class) actualTypeArguments[0];
            registerListener(generic, (EventListener<InsightEvent>) listener);
            break;
        }
    }

    private void registerListener(Class<? extends InsightEvent> type, EventListener<InsightEvent> listener) {
        log.info("register listener {} listen {}", listener.getClass().getName(), type.getName());
        this.listenerMap.computeIfAbsent(type, (k) -> new ArrayList<>()).add(listener);
    }


}
