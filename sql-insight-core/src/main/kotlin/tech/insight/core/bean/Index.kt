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

import org.gongxuanzhang.sql.insight.core.environment.SessionContext
import java.io.File

/**
 * @author gongxuanzhangmelt@gmail.com
 */
interface Index {
    /**
     * before search init method
     */
    fun rndInit()

    /**
     * index id in table.
     * primary key index always 1.
     */
    val id: Int

    /**
     * which table index belong to
     */
    fun belongTo(): Table?

    /**
     * find a cursor from session
     *
     * @return cursor
     */
    fun find(sessionContext: SessionContext?): Cursor?

    /**
     * index name
     */
    val name: String?

    /**
     * insert row to index
     *
     * @param row row
     */
    fun insert(row: InsertRow?)

    /**
     * Index file
     */
    val file: File?

    /**
     * index relative columns
     */
    fun columns(): List<Column?>?
}
