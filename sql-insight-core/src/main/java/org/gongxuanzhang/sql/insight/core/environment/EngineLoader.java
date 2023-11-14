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

package org.gongxuanzhang.sql.insight.core.environment;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.sql.insight.core.engine.storage.StorageEngine;
import org.gongxuanzhang.sql.insight.core.exception.EngineLoadException;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * if you want register your custom engine.
 * add your engine absolute class name in META-INF/engine.properties
 * engine=classname1,classname2,
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
@Slf4j
public class EngineLoader {

    private static final String ENGINE_RESOURCE_LOCATION = "META-INF/engine.properties";

    public static List<StorageEngine> loadEngine() {
        List<StorageEngine> engineList = new ArrayList<>();
        try {
            Enumeration<URL> urls = EngineLoader.class.getClassLoader().getResources(ENGINE_RESOURCE_LOCATION);
            while (urls.hasMoreElements()) {
                Properties engine = new Properties();
                URL url = urls.nextElement();
                UrlResource resource = new UrlResource(url);
                try (InputStream inputStream = resource.getInputStream()) {
                    engine.load(inputStream);
                }
                String engineNames = engine.getProperty("engine");
                String[] engineNameArray = engineNames.split(",");
                for (String name : engineNameArray) {
                    engineList.add(reflectEngine(name));
                }
            }
        } catch (IOException e) {
            log.error("load engine error", e);
        }
        return engineList;
    }

    @SuppressWarnings("all")
    private static StorageEngine reflectEngine(String name) {
        try {
            Class<?> engineClass = Class.forName(name);
            Constructor<?> constructor = engineClass.getConstructor();
            return (StorageEngine) constructor.newInstance();
        } catch (ClassNotFoundException e) {
            throw new EngineLoadException(e, name);
        } catch (NoSuchMethodException e) {
            throw new EngineLoadException(e, name);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new EngineLoadException(e);
        }
    }

}
