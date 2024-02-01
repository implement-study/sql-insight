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
import tech.insight.core.exception.DuplicationEngineNameException

/**
 * storage engine , executor select the engine
 *
 * @author gongxuanzhangmelt@gmail.com
 */
interface StorageEngine {
    /**
     * engine name,sole
     *
     * @return engine name, may be throw
     * @throws [DuplicationEngineNameException]
     */
    val name: String

    /**
     * the file extension that  storage engine create table
     *
     * @return may be a empty list
     * @see [](https://www.mysqlzh.com/doc/147.html)
     */
    fun tableExtensions(): List<String>

    /**
     * open the table and index.
     * It is usually the session that initializes the index
     */
    fun openTable(table: Table)

    /**
     * create table.
     * server layer is responsible for creating table meta data to frm data.
     * storage engine is responsible creating engine index file if necessary.
     */
    fun createTable(table: Table): ResultInterface

    /**
     * truncate table,reserved table construction
     */
    fun truncateTable(table: Table): ResultInterface

    /**
     * insert data
     *
     * @param row insert
     */
    fun insertRow(row: InsertRow)

    /**
     * update
     *
     * @param oldRow hit update condition
     * @param update update set info
     */
    fun update(oldRow: Row, update: UpdateCommand)

    /**
     * delete
     *
     * @param deletedRow hit delete condition
     */
    fun delete(deletedRow: Row)

    /**
     * refresh the data
     */
    fun refresh(table: Table)
}
