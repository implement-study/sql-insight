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

import org.gongxuanzhang.sql.insight.core.annotation.Temporary
import tech.insight.core.annotation.Temporary
import tech.insight.core.bean.Database

/**
 * @author gongxuanzhangmelt@gmail.com
 */
class SessionContext : AbstractMapContext() {
    private var database: Database? = null
    val table: Table? = null
    private val tableAlias: MutableMap<String, String> = HashMap()
    fun tableAlias(name: String, alias: String) {
        tableAlias[name] = alias
    }

    fun getTableByNameOrAlias(nameOrAlias: String): Table? {
        var convertName = tableAlias[nameOrAlias]
        if (convertName == null) {
            convertName = nameOrAlias
        }
        val tableDefinitionManager: TableDefinitionManager =
            SqlInsightContext.Companion.getInstance().getTableDefinitionManager()
        return tableDefinitionManager.select(database.getName(), convertName)
    }

    val currentUser: User?
        get() = null

    fun currentDatabase(): Database? {
        return database
    }

    fun useDatabase(database: Database?) {
        this.database = database
    }

    companion object {
        private val HOLDER = ThreadLocal.withInitial { SessionContext() }

        @get:Temporary(detail = "temp for thread")
        val currentSession: SessionContext
            get() = HOLDER.get()

        @Temporary
        fun clearSession() {
            HOLDER.remove()
        }
    }
}
