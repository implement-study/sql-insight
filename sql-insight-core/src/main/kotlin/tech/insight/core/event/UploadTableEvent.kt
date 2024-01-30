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
package tech.insight.core.event

import org.gongxuanzhang.sql.insight.core.`object`.Table

/**
 * @author gongxuanzhangmelt@gmail.com
 */
class UploadTableEvent
/**
 * @param table after update table
 */
protected constructor(table: Table?) : InsightEvent(table) {
    val table: Table
        get() = source as Table
}
