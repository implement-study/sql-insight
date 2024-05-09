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
import tech.insight.core.bean.value.ValueNull


/**
 * @author gongxuanzhangmelt@gmail.com
 */
class Column : SQLBean {
    lateinit var name: String
    lateinit var dataType: DataType
    var length: Int = -1
    var autoIncrement = false
    var notNull = false
    var primaryKey = false
    var unique = false
    var defaultValue: Value<*> = ValueNull
    var comment: String? = null
    var variable = false
    var nullListIndex = 0


    override fun checkMyself() {
        check(length > 0) { "col length must gather than 0" }
        check(length <= UShort.MAX_VALUE.toInt()) { "col length must less than ${UShort.MAX_VALUE}" }
        if (this.primaryKey) {
            check(defaultValue is ValueNull) { "primary key can't have default value" }
        }
    }


    override fun toString(): String {
        return "Column($name $dataType $length)"
    }

}
