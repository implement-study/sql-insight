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
package tech.insight.core.bean

import com.fasterxml.jackson.annotation.JsonIgnore
import tech.insight.core.engine.storage.StorageEngine
import tech.insight.core.exception.UnknownColumnException


/**
 * @author gongxuanzhangmelt@gmail.com
 */
class Table : SQLBean {
    lateinit var database: Database
    lateinit var name: String
    var columnList: MutableList<Column> = mutableListOf()
    var indexList: MutableList<Index> = mutableListOf()
    lateinit var engine: StorageEngine
    var comment: String = ""
    val databaseName: String
        @JsonIgnore
        get() {
            return database.name
        }

    /**
     * support operator
     */
    val ext = TableExt()
    fun getColumnIndexByName(colName: String): Int {
        return ext.columnIndex[colName] ?: throw UnknownColumnException(colName)
    }

    fun getColumnByName(name: String): Column {
        return ext.columnMap[name] ?: throw UnknownColumnException(name)
    }


    override fun checkMyself() {
        this.columnList.forEachIndexed { index, col ->
            col.checkMyself()
            ext.columnMap[col.name] = col
            ext.columnIndex[col.name] = index
            if (col.primaryKey) {
                this.ext.primaryKeyIndex = index
                this.ext.primaryKeyName = col.name
            }
            if (col.autoIncrement) {
                require(ext.autoColIndex == -1) { "auto increment column can have at most one" }
                ext.autoColIndex = index
            }
            if (col.notNull) {
                ext.notNullIndex.add(index)
            } else {
                ext.nullableColCount++
            }
            if (col.variable) {
                ext.variableIndex.add(index)
            }
        }
    }

}


class TableExt {
    val columnMap: MutableMap<String, Column> = HashMap()
    val columnIndex: MutableMap<String, Int> = HashMap()

    /**
     * not null column index list
     */
    val notNullIndex: MutableList<Int> = ArrayList()
    val variableIndex: MutableList<Int> = ArrayList()
    var autoColIndex = -1
    var primaryKeyIndex = -1
    var nullableColCount = 0
    var primaryKeyName: String? = null
}
