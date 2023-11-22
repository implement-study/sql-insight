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

package org.gongxuanzhang.sql.insight.core.engine.storage;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.sql.insight.core.engine.StorageEngineManager;
import org.gongxuanzhang.sql.insight.core.environment.GlobalContext;
import org.gongxuanzhang.sql.insight.core.event.EventListener;
import org.gongxuanzhang.sql.insight.core.event.EventPublisher;
import org.gongxuanzhang.sql.insight.core.event.MultipleEventListener;
import org.gongxuanzhang.sql.insight.core.exception.DuplicationEngineNameException;
import org.gongxuanzhang.sql.insight.core.exception.EngineNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.gongxuanzhang.sql.insight.core.environment.DefaultProperty.DEFAULT_ENGINE;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
@Slf4j
public class SimpleStorageEngineManager implements StorageEngineManager {

    private final Map<String, StorageEngine> storageEngineMap = new ConcurrentHashMap<>();

    @Override
    public List<StorageEngine> allEngine() {
        return new ArrayList<>(storageEngineMap.values());
    }

    @Override
    public void registerEngine(StorageEngine engine) {
        log.info("register engine [{}], class {}", engine.getName(), engine.getClass().getName());
        if (storageEngineMap.putIfAbsent(engine.getName().toUpperCase(), engine) != null) {
            throw new DuplicationEngineNameException("engine " + engine.getName() + "already register ");
        }
        EventPublisher publisher = EventPublisher.getInstance();
        if (engine instanceof MultipleEventListener) {
            publisher.registerListener((MultipleEventListener) engine);
        } else if (engine instanceof EventListener) {
            publisher.registerListener((EventListener<?>) engine);
        }
    }

    @Override
    public StorageEngine selectEngine(String engineName) {
        if (engineName == null || engineName.isEmpty()) {
            return selectEngine(GlobalContext.getInstance().get(DEFAULT_ENGINE.getKey()));
        }
        StorageEngine engine = storageEngineMap.get(engineName.toUpperCase());
        if (engine == null) {
            throw new EngineNotFoundException(engineName);
        }
        return engine;
    }
}
