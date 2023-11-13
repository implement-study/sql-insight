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


import org.gongxuanzhang.sql.insight.core.engine.StorageEngineManager;
import org.gongxuanzhang.sql.insight.core.engine.storage.SimpleStorageEngineManager;
import org.gongxuanzhang.sql.insight.core.engine.storage.StorageEngine;
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
 * a static context,contains all of necessary component
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public class SqlInsightContext {

    private SqlInsightContext() {

    }

    private StorageEngineManager engineManager;

    private GlobalContext globalContext;


    private static final SqlInsightContext INSTANCE = createSqlInsightContext();

    private static SqlInsightContext createSqlInsightContext() {
        SqlInsightContext context = new SqlInsightContext();
        context.engineManager = new SimpleStorageEngineManager();
        context.globalContext = GlobalContext.getInstance();
        List<String> engineClass = new ArrayList<>();
        try {
            Enumeration<URL> urls = SqlInsightContext.class.getClassLoader().getResources("META-INF/engine.properties");
            while (urls.hasMoreElements()) {
                Properties engine = new Properties();
                URL url = urls.nextElement();
                UrlResource resource = new UrlResource(url);
                try(InputStream inputStream = resource.getInputStream()){
                    engine.load(inputStream);
                }
                engineClass.add(engine.getProperty("engine"));

            }
        } catch (IOException ignore) {

        }
        for (String engine : engineClass) {
            String[] split = engine.split(",");
            for (String name : split) {
                try {
                    Class<?> aClass = Class.forName(name);
                    Constructor<?> constructor = aClass.getConstructor();
                    context.engineManager.registerEngine((StorageEngine) constructor.newInstance());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(name + "can't instance ");
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(name + "must have a not params constructor method");
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return context;
    }


    public static SqlInsightContext getInstance() {
        return INSTANCE;
    }

    public StorageEngineManager getEngineManager() {
        return engineManager;
    }

    public GlobalContext getGlobalContext() {
        return globalContext;
    }

    public StorageEngine selectEngine(String engineName) {
        return engineManager.selectEngine(engineName);
    }
}
