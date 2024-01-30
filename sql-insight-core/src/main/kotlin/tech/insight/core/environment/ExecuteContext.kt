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

import tech.insight.core.bean.Row
import tech.insight.core.result.ResultInterface

/**
 * context during execute
 *
 * @author gongxuanzhangmelt@gmail.com
 */
class ExecuteContext(val sql: String) : AbstractMapContext() {
    private val rows: MutableList<Row> = ArrayList<Row>()
    fun getRows(): List<Row> {
        return rows
    }

    fun toResult(): ResultInterface {
        return SelectResult(rows)
    }

    fun addRow(row: Row) {
        rows.add(row)
    }

    val tableDefinitionManager: TableDefinitionManager?
        get() = sqlInsightContext.tableDefinitionManager
    val sqlInsightContext: SqlInsightContext
        get() = SqlInsightContext.Companion.getInstance()
    val globalContext: GlobalContext
        get() = GlobalContext.Companion.getInstance()
    val sessionContext: SessionContext
        get() = SessionContext.Companion.getCurrentSession()

    override fun get(key: String?): String? {
        val s = sessionContext[key]
        return s ?: globalContext[key]
    }

    override fun put(key: String?, value: String?) {
        throw UnsupportedOperationException("execute context can't support put method")
    }

    override fun remove(key: String?): String? {
        throw UnsupportedOperationException("execute context can't support remove method")
    }
}
