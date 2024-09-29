/*
 * Copyright 2023 sql-insight  and the original author or authors <gongxuanzhangmelt@gmail.com>.
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
package tech.insight.engine.innodb.index

import java.io.File
import tech.insight.core.bean.Column
import tech.insight.core.bean.InsertRow
import tech.insight.core.bean.Row
import tech.insight.core.bean.Table
import tech.insight.core.engine.AutoIncrementKeyCounter
import tech.insight.core.exception.DataTooLongException
import tech.insight.engine.innodb.core.InnodbIc
import tech.insight.engine.innodb.page.Constant
import tech.insight.engine.innodb.page.IndexKey
import tech.insight.engine.innodb.page.PrimaryKey
import tech.insight.engine.innodb.page.compact.Compact
import tech.insight.engine.innodb.page.compact.RowFormatFactory

/**
 * @author gongxuanzhangmelt@gmail.com
 */
class ClusteredIndex(table: Table) : InnodbIndex() {

    override val file: File by lazy {
        val dbFolder = table.database.dbFolder
        File(dbFolder, table.name + ".idb")
    }

    init {
        this.table = table
    }

    private lateinit var autoIncrementKeyCounter: AutoIncrementKeyCounter

    override fun columns(): List<Column> {
        return listOf(table.columnList[table.ext.primaryKeyIndex])
    }

    /**
     * init index
     * init auto increment key counter
     * read the root page to buffer
     *
     */
    override fun rndInit() {
        if (table.ext.autoColIndex >= 0) {
            autoIncrementKeyCounter = InnodbIc(table)
        }
    }

    override val id: Int = 1

    override val name: String
        get() = ""

    override val isClusteringIndex: Boolean = true


    override fun insert(row: InsertRow) {
        autoIncrementKeyCounter.dealAutoIncrement(row)
        val compact: Compact = RowFormatFactory.compactFromNormalRow(row)
        if (compact.length() >= Constant.COMPACT_MAX_ROW_LENGTH) {
            throw DataTooLongException("compact row can't greater than " + Constant.COMPACT_MAX_ROW_LENGTH)
        }
        compact.belongIndex = this
        rootPage.insertData(compact)
    }

    override fun findByKey(key: IndexKey): Row {
        require(key is PrimaryKey) { "clustered index must search by Primary key " }
        TODO()
    }


    override fun hashCode(): Int {
        return table().hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ClusteredIndex) return false
        return this.table() == other.table()
    }
}
