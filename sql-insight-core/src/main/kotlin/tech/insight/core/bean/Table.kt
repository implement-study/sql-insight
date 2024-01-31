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

import tech.insight.core.exception.UnknownColumnException


/**
 * @author gongxuanzhangmelt@gmail.com
 */
class Table : SQLBean {
    lateinit var database: Database
    lateinit var name: String
    var columnList: MutableList<Column> = mutableListOf()
    var indexList: MutableList<Index> = mutableListOf()
    lateinit var engine: String
    var comment: String = ""
    val databaseName: String
        get() {
            return database.name
        }

    //  support operator
    val ext = TableExt()
    fun getColumnIndexByName(colName: String): Int {
        return ext.columnIndex[colName] ?: throw UnknownColumnException(colName)
    }

    fun getColumnByName(name: String): Column {
        return ext.columnMap[name] ?: throw UnknownColumnException(name)
    }


    override fun checkMyself() {
        TODO("Not yet implemented")
    }

}
