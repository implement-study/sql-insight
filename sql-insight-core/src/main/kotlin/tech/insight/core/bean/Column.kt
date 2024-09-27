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
package tech.insight.core.bean

import tech.insight.core.bean.value.Value


/**
 * @author gongxuanzhangmelt@gmail.com
 */
class Column constructor(
    var name: String,
    val dataType: DataType,
    val length: Int,
    val autoIncrement: Boolean,
    val notNull: Boolean,
    val primaryKey: Boolean,
    val unique: Boolean,
    val hasDefault: Boolean,
    val defaultValue: Value<*>,
    val comment: String? = null,
    val nullListIndex: Int = -1 //  greater -1 means the column can be null
) : SQLBean {
    val variable = dataType == DataType.VARCHAR

    override fun parent(): SQLBean? {
        TODO("Not yet implemented")
    }


    override fun toString(): String {
        return "Column($name $dataType $length)"
    }


}
