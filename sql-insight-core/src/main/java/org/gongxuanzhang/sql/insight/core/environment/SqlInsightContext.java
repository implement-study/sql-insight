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

    private TableDefinitionManager tableDefinitionManager;

    private static final SqlInsightContext INSTANCE = createSqlInsightContext();

    private static SqlInsightContext createSqlInsightContext() {
        SqlInsightContext context = new SqlInsightContext();
        context.engineManager = new SimpleStorageEngineManager();
        context.globalContext = GlobalContext.getInstance();
        context.tableDefinitionManager = new TableDefinitionManager();
        EngineLoader.loadEngine().forEach(context.engineManager::registerEngine);
        TableLoader.loadTable().forEach(context.tableDefinitionManager::load);
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

    public TableDefinitionManager getTableDefinitionManager() {
        return tableDefinitionManager;
    }
}
