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
package tech.insight.core.bean.value

import lombok.EqualsAndHashCode

/**
 * @author gongxuanzhangmelt@gmail.com
 */
@EqualsAndHashCode
class ValueVarchar : DynamicValue {
    private val value: String

    constructor(value: Value) {
        this.value = value.getSource().toString()
    }

    constructor(value: String) {
        this.value = value
    }

    override val length: Int
        get() = toBytes().size
    override val source: Any?
        get() = value

    override fun toBytes(): ByteArray {
        return value.toByteArray()
    }

    override fun compareTo(o: Value): Int {
        return value.compareTo(o.getSource().toString())
    }
}
