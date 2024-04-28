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
package tech.insight.engine.innodb.index

import tech.insight.core.bean.*
import tech.insight.core.engine.AutoIncrementKeyCounter
import tech.insight.core.environment.Session
import tech.insight.core.exception.DataTooLongException
import tech.insight.engine.innodb.core.InnodbIc
import tech.insight.engine.innodb.page.Constant
import tech.insight.engine.innodb.page.IndexKey
import tech.insight.engine.innodb.page.PrimaryKey
import tech.insight.engine.innodb.page.compact.Compact
import tech.insight.engine.innodb.page.compact.RowFormatFactory
import java.io.File

/**
 * @author gongxuanzhangmelt@gmail.com
 */
class ClusteredIndex(table: Table) : InnodbIndex() {
    init {
        this.table = table
    }


    override val file: File by lazy {
        val dbFolder = table.database.dbFolder
        File(dbFolder, table.name + ".idb")
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

    override fun find(session: Session): Cursor {
        return InnodbClusteredCursor(this, session)
    }

    override val name: String
        get() = ""


    override fun insert(row: InsertRow) {
        if (autoIncrementKeyCounter.dealAutoIncrement(row)) {
            debug("auto increment primary key ${table.columnList[table.ext.autoColIndex].name}")
        }
        val compact: Compact = RowFormatFactory.compactFromInsertRow(row)
        val root = rootPage
        if (compact.length() >= Constant.COMPACT_MAX_ROW_LENGTH) {
            throw DataTooLongException("compact row can't greater than " + Constant.COMPACT_MAX_ROW_LENGTH)
        }
        root.insertData(compact)
    }

    override fun findByKey(key: IndexKey): Row {
        require(key is PrimaryKey) { "clustered index must search by Primary key " }
        TODO()
    }


}
