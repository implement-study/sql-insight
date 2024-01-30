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
package tech.insight.core.environment

import org.gongxuanzhang.sql.insight.core.engine.StorageEngineManager
import java.util.function.Consumer

/**
 * a static context,contains all of necessary component
 *
 * @author gongxuanzhangmelt@gmail.com
 */
class SqlInsightContext private constructor() {
    private var engineManager: StorageEngineManager? = null
    var globalContext: GlobalContext? = null
        private set
    var tableDefinitionManager: TableDefinitionManager? = null
        private set

    fun getEngineManager(): StorageEngineManager? {
        return engineManager
    }

    fun selectEngine(engineName: String?): StorageEngine {
        return engineManager.selectEngine(engineName)
    }

    companion object {
        val instance = createSqlInsightContext()
        private fun createSqlInsightContext(): SqlInsightContext {
            val context = SqlInsightContext()
            context.engineManager = SimpleStorageEngineManager()
            context.globalContext = GlobalContext.Companion.getInstance()
            context.tableDefinitionManager = TableDefinitionManager()
            EngineLoader.loadEngine().forEach(context.engineManager::registerEngine)
            TableLoader.loadTable()
                .forEach(Consumer<Table> { table: Table -> context.tableDefinitionManager!!.load(table) })
            EventPublisher.getInstance().registerListener(context.tableDefinitionManager)
            return context
        }
    }
}
