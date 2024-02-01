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
package tech.insight.core.engine.storage

import tech.insight.core.bean.InsertRow
import tech.insight.core.bean.Row
import tech.insight.core.bean.Table
import tech.insight.core.command.UpdateCommand
import tech.insight.core.result.ResultInterface

/**
 * template for storage engine
 *
 * @author gongxuanzhangmelt@gmail.com
 */
abstract class AbstractStorageEngine : StorageEngine {
    override fun tableExtensions(): List<String> {
        return emptyList()
    }

    override fun openTable(table: Table) {

    }

    override fun createTable(table: Table): ResultInterface {
        throw UnsupportedOperationException()
    }

    override fun truncateTable(table: Table): ResultInterface {
        throw UnsupportedOperationException()
    }

    override fun insertRow(row: InsertRow) {
        throw UnsupportedOperationException()
    }

    override fun update(oldRow: Row, update: UpdateCommand) {
        throw UnsupportedOperationException()
    }

    override fun delete(deletedRow: Row) {
        throw UnsupportedOperationException()
    }

    override fun refresh(table: Table) {}
}
