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
package tech.insight.core.engine

import tech.insight.core.bean.Database
import tech.insight.core.bean.InsertRow
import tech.insight.core.bean.Table


/**
 * every engine that support auto increment should have a counter.
 * engine allow not support auto increment col.
 *
 * @author gongxuanzhangmelt@gmail.com
 */
interface AutoIncrementKeyCounter {
    /**
     * before insert row. check data auto increment column value is empty.
     * if not empty the counter should refresh perhaps.
     * if value is empty the counter should set a increment value
     *
     * @param row insert row
     * @return increment succeed
     */
    fun dealAutoIncrement(row: InsertRow): Boolean

    /**
     * reset the counter
     */
    fun reset(table: Table)

    /**
     * reset the Database
     */
    fun reset(database: Database)
}
